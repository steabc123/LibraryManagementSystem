package com.library.servlet;

import com.library.common.Result;
import com.library.dao.CartDAO;
import com.library.dao.OrderDAO;
import com.library.entity.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/orders/*")
public class OrderServlet extends BaseServlet {
    private OrderDAO orderDAO = new OrderDAO();
    private CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkLogin(req, resp)) {
            return;
        }

        UserInfo currentUser = getCurrentUser(req);
        String pathInfo = req.getPathInfo();

        // 获取当前用户的订单列表
        if ("/my".equals(pathInfo)) {
            List<OrderInfo> orders = orderDAO.getOrdersByUserId(currentUser.getId());
            sendJsonResponse(resp, Result.success(orders));
            return;
        }

        // 获取所有订单（管理员）
        if ("/all".equals(pathInfo)) {
            if (!"admin".equals(currentUser.getRole())) {
                sendJsonResponse(resp, Result.error(403, "权限不足"));
                return;
            }
            List<OrderInfo> orders = orderDAO.getAllOrders();
            sendJsonResponse(resp, Result.success(orders));
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
                        sendJsonResponse(resp, Result.error(403, "无权查看此订单"));
                        return;
                    }
                    sendJsonResponse(resp, Result.success(order));
                } else {
                    sendJsonResponse(resp, Result.error("订单不存在"));
                }
                return;
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的订单ID"));
                return;
            }
        }

        sendJsonResponse(resp, Result.error("无效请求"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkLogin(req, resp)) {
            return;
        }

        UserInfo currentUser = getCurrentUser(req);
        Map<String, Object> requestData = gson.fromJson(readRequestBody(req), Map.class);
        String action = (String) requestData.get("action");

        // 创建订单（从购物车结算）
        if ("create".equals(action)) {
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) requestData.get("items");
            
            if (itemsData == null || itemsData.isEmpty()) {
                sendJsonResponse(resp, Result.error("购物车为空"));
                return;
            }

            // 计算总金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (Map<String, Object> itemData : itemsData) {
                if (itemData.get("bookId") == null || itemData.get("price") == null || itemData.get("quantity") == null) {
                    sendJsonResponse(resp, Result.error("订单项数据不完整"));
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

            OrderInfo order = new OrderInfo();
            order.setUserId(currentUser.getId());
            order.setTotalAmount(totalAmount);
            order.setOrderStatus(0);
            order.setCreateTime(LocalDateTime.now());

            if (orderDAO.createOrder(order, orderItems)) {
                cartDAO.clearCart(currentUser.getId());
                
                Map<String, Object> data = new HashMap<>();
                data.put("orderId", order.getId());
                sendJsonResponse(resp, Result.success(data));
            } else {
                sendJsonResponse(resp, Result.error("创建订单失败"));
            }
            return;
        }

        // 支付订单
        if ("pay".equals(action)) {
            Long orderId = ((Number) requestData.get("orderId")).longValue();
            
            OrderInfo order = orderDAO.getOrderById(orderId);
            if (order == null) {
                sendJsonResponse(resp, Result.error("订单不存在"));
                return;
            }
            
            if (!order.getUserId().equals(currentUser.getId()) && !"admin".equals(currentUser.getRole())) {
                sendJsonResponse(resp, Result.error("无权操作此订单"));
                return;
            }
            
            if (order.getOrderStatus() != 0) {
                sendJsonResponse(resp, Result.error("订单状态不正确"));
                return;
            }

            if (orderDAO.updateOrderStatus(orderId, 1, "pay_time")) {
                sendJsonResponse(resp, Result.success("支付成功"));
            } else {
                sendJsonResponse(resp, Result.error("支付失败"));
            }
            return;
        }

        // 发货（管理员）
        if ("ship".equals(action)) {
            if (!"admin".equals(currentUser.getRole())) {
                sendJsonResponse(resp, Result.error(403, "权限不足"));
                return;
            }

            Long orderId = ((Number) requestData.get("orderId")).longValue();
            
            OrderInfo order = orderDAO.getOrderById(orderId);
            if (order == null) {
                sendJsonResponse(resp, Result.error("订单不存在"));
                return;
            }
            
            if (order.getOrderStatus() != 1) {
                sendJsonResponse(resp, Result.error("订单状态不正确，只有已支付的订单才能发货"));
                return;
            }

            if (orderDAO.updateOrderStatus(orderId, 2, "ship_time")) {
                sendJsonResponse(resp, Result.success("发货成功"));
            } else {
                sendJsonResponse(resp, Result.error("发货失败"));
            }
            return;
        }

        // 确认收货
        if ("receive".equals(action)) {
            Long orderId = ((Number) requestData.get("orderId")).longValue();
            
            OrderInfo order = orderDAO.getOrderById(orderId);
            if (order == null) {
                sendJsonResponse(resp, Result.error("订单不存在"));
                return;
            }
            
            if (!order.getUserId().equals(currentUser.getId())) {
                sendJsonResponse(resp, Result.error("无权操作此订单"));
                return;
            }
            
            if (order.getOrderStatus() != 2) {
                sendJsonResponse(resp, Result.error("订单状态不正确，只有已发货的订单才能确认收货"));
                return;
            }

            if (orderDAO.updateOrderStatus(orderId, 3, "receive_time")) {
                sendJsonResponse(resp, Result.success("确认收货成功"));
            } else {
                sendJsonResponse(resp, Result.error("确认收货失败"));
            }
            return;
        }

        // 取消订单
        if ("cancel".equals(action)) {
            Long orderId = ((Number) requestData.get("orderId")).longValue();
            
            OrderInfo order = orderDAO.getOrderById(orderId);
            if (order == null) {
                sendJsonResponse(resp, Result.error("订单不存在"));
                return;
            }
            
            if (!order.getUserId().equals(currentUser.getId()) && !"admin".equals(currentUser.getRole())) {
                sendJsonResponse(resp, Result.error("无权操作此订单"));
                return;
            }
            
            if (order.getOrderStatus() != 0) {
                sendJsonResponse(resp, Result.error("只能取消未支付的订单"));
                return;
            }

            if (orderDAO.deleteOrder(orderId)) {
                sendJsonResponse(resp, Result.success("取消订单成功"));
            } else {
                sendJsonResponse(resp, Result.error("取消订单失败"));
            }
            return;
        }

        sendJsonResponse(resp, Result.error("无效操作"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkAdmin(req, resp)) {
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Long orderId = Long.parseLong(pathInfo.substring(1));
                if (orderDAO.deleteOrder(orderId)) {
                    sendJsonResponse(resp, Result.success("删除成功"));
                } else {
                    sendJsonResponse(resp, Result.error("删除订单失败"));
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的订单ID"));
            }
        } else {
            sendJsonResponse(resp, Result.error("请提供订单ID"));
        }
    }
}
