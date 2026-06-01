package com.library.servlet;

import com.library.common.Result;
import com.library.dao.CartDAO;
import com.library.entity.CartItem;
import com.library.entity.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/cart/*")
public class CartServlet extends BaseServlet {
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
        
        if ("/list".equals(pathInfo)) {
            List<CartItem> cartItems = cartDAO.getCartByUserId(currentUser.getId());
            sendJsonResponse(resp, Result.success(cartItems));
        } else if ("/total".equals(pathInfo)) {
            double totalAmount = cartDAO.calculateTotalAmount(currentUser.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("totalAmount", totalAmount);
            sendJsonResponse(resp, Result.success(data));
        } else {
            sendJsonResponse(resp, Result.error("无效请求"));
        }
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

        if ("add".equals(action)) {
            Integer bookId = ((Number) requestData.get("bookId")).intValue();
            String bookName = (String) requestData.get("bookName");
            String author = (String) requestData.get("author");
            Double price = ((Number) requestData.get("price")).doubleValue();
            Integer quantity = requestData.get("quantity") != null ? 
                ((Number) requestData.get("quantity")).intValue() : 1;

            if (cartDAO.addToCart(currentUser.getId(), bookId, bookName, author, price, quantity)) {
                sendJsonResponse(resp, Result.success("添加成功"));
            } else {
                sendJsonResponse(resp, Result.error("添加失败"));
            }
        } else if ("clear".equals(action)) {
            if (cartDAO.clearCart(currentUser.getId())) {
                sendJsonResponse(resp, Result.success("清空成功"));
            } else {
                sendJsonResponse(resp, Result.error("清空失败"));
            }
        } else {
            sendJsonResponse(resp, Result.error("无效操作"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkLogin(req, resp)) {
            return;
        }

        Map<String, Object> requestData = gson.fromJson(readRequestBody(req), Map.class);
        Integer cartItemId = ((Number) requestData.get("cartItemId")).intValue();
        Integer quantity = ((Number) requestData.get("quantity")).intValue();

        if (cartDAO.updateCartItemQuantity(cartItemId, quantity)) {
            sendJsonResponse(resp, Result.success("更新成功"));
        } else {
            sendJsonResponse(resp, Result.error("更新失败"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkLogin(req, resp)) {
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                Integer cartItemId = Integer.parseInt(pathInfo.substring(1));
                if (cartDAO.deleteCartItem(cartItemId)) {
                    sendJsonResponse(resp, Result.success("删除成功"));
                } else {
                    sendJsonResponse(resp, Result.error("删除失败"));
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的购物车项ID"));
            }
        } else {
            sendJsonResponse(resp, Result.error("请指定要删除的购物车项"));
        }
    }
}
