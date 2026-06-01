package com.library.servlet;

import com.google.gson.Gson;
import com.library.common.Result;
import com.library.dao.CartDAO;
import com.library.dao.OrderDAO;
import com.library.entity.*;
import com.library.util.GsonConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/orders/*")
public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();
    private Gson gson = GsonConfig.getGson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.getWriter().write(gson.toJson(Result.error(401, "未登录")));
            return;
        }

        UserInfo currentUser = (UserInfo) session.getAttribute("currentUser");
        String pathInfo = req.getPathInfo();

        // 获取当前用户的订单列表
        if ("/my".equals(pathInfo)) {
            List<OrderInfo> orders = orderDAO.getOrdersByUserId(currentUser.getId());
            resp.getWriter().write(gson.toJson(Result.success(orders)));
            return;
        }

        // 获取所有订单（管理员）
        if ("/all".equals(pathInfo)) {
            if (!"admin".equals(currentUser.getRole())) {
                resp.getWriter().write(gson.toJson(Result.error(403, "权限不足")));
                return;
            }
            List<OrderInfo> orders = orderDAO.getAllOrders();
            resp.getWriter().write(gson.toJson(Result.success(orders)));
            return;
        }

        // 获取指定订单详情
        if (pathInfo != null && pathInfo.matches("/\\d+")) {
            try {
                Long orderId = Long.parseLong(pathInfo.substring(1));
                OrderInfo order = orderDAO.getOrderById(orderId);
                if (order != null) {
                    // 权限检查：普通用户只能查看自己的订单
                    if (!"admin".equals(currentUser.getRole()) && !order.getUserId().equals(currentUser.getId())) {
                        resp.getWriter().write(gson.toJson(Result.error(403, "无权查看此订单")));
                        return;
                    }
                    resp.getWriter().write(gson.toJson(Result.success(order)));
                } else {
                    resp.getWriter().write(gson.toJson(Result.error("订单不存在")));
                }
                return;
            } catch (NumberFormatException e) {
                resp.getWriter().write(gson.toJson(Result.error("无效的订单ID")));
                return;
            }
        }

        resp.getWriter().write(gson.toJson(Result.error("无效请求")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.getWriter().write(gson.toJson(Result.error(401, "未登录")));
            return;
        }

        UserInfo currentUser = (UserInfo) session.getAttribute("currentUser");

        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        Map<String, Object> requestData = gson.fromJson(sb.toString(), Map.class);
        String action = (String) requestData.get("action");

        // 创建订单（从购物车结算）
        if ("create".equals(action)) {
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) requestData.get("items");
            
            if (itemsData == null || itemsData.isEmpty()) {
                resp.getWriter().write(gson.toJson(Result.error("购物车为空")));
                return;
            }

            // 计算总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (Map<String, Object> itemData : itemsData) {
                // 安全检查每个字段
                if (itemData.get("bookId") == null || itemData.get("price") == null || itemData.get("quantity") == null) {
                    resp.getWriter().write(gson.toJson(Result.error("订单项数据不完整")));
                    return;
                }
                
                Integer bookId = ((Number) itemData.get("bookId")).intValue();
                String bookName = (String) itemData.get("bookName");
                String author = (String) itemData.get("author");
                Double price = ((Number) itemData.get("price")).doubleValue();
                Integer quantity = ((Number) itemData.get("quantity")).intValue();
                
                BigDecimal itemPrice = new BigDecimal(price);
                totalAmount = totalAmount.add(itemPrice.multiply(new BigDecimal(quantity)));
                
                OrderItem item = new OrderItem();
                item.setBookId(bookId);
                item.setBookName(bookName);
                item.setAuthor(author);
                item.setPrice(itemPrice);
                item.setQuantity(quantity);
                orderItems.add(item);
            }

            // 创建订单
            OrderInfo order = new OrderInfo();
            order.setUserId(currentUser.getId());
            order.setTotalAmount(totalAmount);
            order.setOrderStatus(0); // 未支付
            order.setCreateTime(LocalDateTime.now());

            if (orderDAO.createOrder(order, orderItems)) {
                // 清空购物车
                cartDAO.clearCart(currentUser.getId());
                
                Map<String, Object> data = new HashMap<>();
                data.put("orderId", order.getId());
                resp.getWriter().write(gson.toJson(Result.success(data)));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("创建订单失败")));
            }
            return;
        }

        // 支付订单
        if ("pay".equals(action)) {
            Object orderIdObj = requestData.get("orderId");
            if (orderIdObj == null) {
                resp.getWriter().write(gson.toJson(Result.error("订单ID不能为空")));
                return;
            }
            Long orderId = ((Number) orderIdObj).longValue();
            
            // 验证订单归属
            OrderInfo order = orderDAO.getOrderById(orderId);
            if (order == null) {
                resp.getWriter().write(gson.toJson(Result.error("订单不存在")));
                return;
            }
            
            if (!order.getUserId().equals(currentUser.getId()) && !"admin".equals(currentUser.getRole())) {
                resp.getWriter().write(gson.toJson(Result.error("无权操作此订单")));
                return;
            }
            
            if (order.getOrderStatus() != 0) {
                resp.getWriter().write(gson.toJson(Result.error("订单状态不正确")));
                return;
            }

            if (orderDAO.updateOrderStatus(orderId, 1, "pay_time")) {
                resp.getWriter().write(gson.toJson(Result.success("支付成功")));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("支付失败")));
            }
            return;
        }

        // 发货（管理员）
        if ("ship".equals(action)) {
            if (!"admin".equals(currentUser.getRole())) {
                resp.getWriter().write(gson.toJson(Result.error(403, "权限不足")));
                return;
            }

            Object orderIdObj = requestData.get("orderId");
            if (orderIdObj == null) {
                resp.getWriter().write(gson.toJson(Result.error("订单ID不能为空")));
                return;
            }
            Long orderId = ((Number) orderIdObj).longValue();
            
            OrderInfo order = orderDAO.getOrderById(orderId);
            if (order == null) {
                resp.getWriter().write(gson.toJson(Result.error("订单不存在")));
                return;
            }
            
            if (order.getOrderStatus() != 1) {
                resp.getWriter().write(gson.toJson(Result.error("订单状态不正确，只有已支付的订单才能发货")));
                return;
            }

            if (orderDAO.updateOrderStatus(orderId, 2, "ship_time")) {
                resp.getWriter().write(gson.toJson(Result.success("发货成功")));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("发货失败")));
            }
            return;
        }

        // 确认收货
        if ("receive".equals(action)) {
            Object orderIdObj = requestData.get("orderId");
            if (orderIdObj == null) {
                resp.getWriter().write(gson.toJson(Result.error("订单ID不能为空")));
                return;
            }
            Long orderId = ((Number) orderIdObj).longValue();
            
            OrderInfo order = orderDAO.getOrderById(orderId);
            if (order == null) {
                resp.getWriter().write(gson.toJson(Result.error("订单不存在")));
                return;
            }
            
            if (!order.getUserId().equals(currentUser.getId())) {
                resp.getWriter().write(gson.toJson(Result.error("无权操作此订单")));
                return;
            }
            
            if (order.getOrderStatus() != 2) {
                resp.getWriter().write(gson.toJson(Result.error("订单状态不正确，只有已发货的订单才能确认收货")));
                return;
            }

            if (orderDAO.updateOrderStatus(orderId, 3, "receive_time")) {
                resp.getWriter().write(gson.toJson(Result.success("确认收货成功")));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("确认收货失败")));
            }
            return;
        }

        // 取消订单
        if ("cancel".equals(action)) {
            Object orderIdObj = requestData.get("orderId");
            if (orderIdObj == null) {
                resp.getWriter().write(gson.toJson(Result.error("订单ID不能为空")));
                return;
            }
            Long orderId = ((Number) orderIdObj).longValue();
            
            OrderInfo order = orderDAO.getOrderById(orderId);
            if (order == null) {
                resp.getWriter().write(gson.toJson(Result.error("订单不存在")));
                return;
            }
            
            if (!order.getUserId().equals(currentUser.getId()) && !"admin".equals(currentUser.getRole())) {
                resp.getWriter().write(gson.toJson(Result.error("无权操作此订单")));
                return;
            }
            
            if (order.getOrderStatus() != 0) {
                resp.getWriter().write(gson.toJson(Result.error("只能取消未支付的订单")));
                return;
            }

            if (orderDAO.deleteOrder(orderId)) {
                resp.getWriter().write(gson.toJson(Result.success("取消订单成功")));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("取消订单失败")));
            }
            return;
        }

        resp.getWriter().write(gson.toJson(Result.error("无效操作")));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.getWriter().write(gson.toJson(Result.error(401, "未登录")));
            return;
        }

        UserInfo currentUser = (UserInfo) session.getAttribute("currentUser");

        // 只有管理员可以删除订单
        if (!"admin".equals(currentUser.getRole())) {
            resp.getWriter().write(gson.toJson(Result.error(403, "权限不足")));
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long orderId = Long.parseLong(pathInfo.substring(1));
                if (orderDAO.deleteOrder(orderId)) {
                    resp.getWriter().write(gson.toJson(Result.success("删除成功")));
                } else {
                    resp.getWriter().write(gson.toJson(Result.error("删除订单失败")));
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write(gson.toJson(Result.error("无效的订单ID")));
            }
        } else {
            resp.getWriter().write(gson.toJson(Result.error("请提供订单ID")));
        }
    }
}
