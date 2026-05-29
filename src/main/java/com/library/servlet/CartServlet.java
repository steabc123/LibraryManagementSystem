package com.library.servlet;

import com.google.gson.Gson;
import com.library.common.Result;
import com.library.dao.CartDAO;
import com.library.entity.CartItem;
import com.library.entity.UserInfo;
import com.library.util.GsonConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {
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

        // 获取当前用户ID
        UserInfo currentUser = (UserInfo) session.getAttribute("currentUser");
        Integer userId = currentUser.getId();

        String pathInfo = req.getPathInfo();
        
        if ("/list".equals(pathInfo)) {
            // 获取购物车列表
            List<CartItem> cartItems = cartDAO.getCartByUserId(userId);
            resp.getWriter().write(gson.toJson(Result.success(cartItems)));
        } else if ("/total".equals(pathInfo)) {
            // 计算购物车总金额
            double totalAmount = cartDAO.calculateTotalAmount(userId);
            Map<String, Object> data = new HashMap<>();
            data.put("totalAmount", totalAmount);
            resp.getWriter().write(gson.toJson(Result.success(data)));
        } else {
            resp.getWriter().write(gson.toJson(Result.error("无效请求")));
        }
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

        // 获取当前用户ID
        UserInfo currentUser = (UserInfo) session.getAttribute("currentUser");
        Integer userId = currentUser.getId();

        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        Map<String, Object> requestData = gson.fromJson(sb.toString(), Map.class);
        String action = (String) requestData.get("action");

        if ("add".equals(action)) {
            // 添加到购物车
            Integer bookId = ((Number) requestData.get("bookId")).intValue();
            String bookName = (String) requestData.get("bookName");
            String author = (String) requestData.get("author");
            Double price = ((Number) requestData.get("price")).doubleValue();
            Integer quantity = requestData.get("quantity") != null ? 
                ((Number) requestData.get("quantity")).intValue() : 1;

            if (cartDAO.addToCart(userId, bookId, bookName, author, price, quantity)) {
                resp.getWriter().write(gson.toJson(Result.success("添加成功")));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("添加失败")));
            }
        } else if ("clear".equals(action)) {
            // 清空购物车
            if (cartDAO.clearCart(userId)) {
                resp.getWriter().write(gson.toJson(Result.success("清空成功")));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("清空失败")));
            }
        } else {
            resp.getWriter().write(gson.toJson(Result.error("无效操作")));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.getWriter().write(gson.toJson(Result.error(401, "未登录")));
            return;
        }

        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        Map<String, Object> requestData = gson.fromJson(sb.toString(), Map.class);
        Integer cartItemId = ((Number) requestData.get("cartItemId")).intValue();
        Integer quantity = ((Number) requestData.get("quantity")).intValue();

        if (cartDAO.updateCartItemQuantity(cartItemId, quantity)) {
            resp.getWriter().write(gson.toJson(Result.success("更新成功")));
        } else {
            resp.getWriter().write(gson.toJson(Result.error("更新失败")));
        }
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

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Integer cartItemId = Integer.parseInt(pathInfo.substring(1));
                if (cartDAO.deleteCartItem(cartItemId)) {
                    resp.getWriter().write(gson.toJson(Result.success("删除成功")));
                } else {
                    resp.getWriter().write(gson.toJson(Result.error("删除失败")));
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write(gson.toJson(Result.error("无效的购物车项ID")));
            }
        } else {
            resp.getWriter().write(gson.toJson(Result.error("请指定要删除的购物车项")));
        }
    }
}
