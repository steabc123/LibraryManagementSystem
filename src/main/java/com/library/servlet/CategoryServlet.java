package com.library.servlet;

import com.library.common.Result;
import com.library.dao.CategoryDAO;
import com.library.entity.BookCategory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/categories/*")
public class CategoryServlet extends BaseServlet {
    private CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<BookCategory> categories = categoryDAO.getAllCategories();
            sendJsonResponse(resp, Result.success(categories));
        } else {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                BookCategory category = categoryDAO.getCategoryById(id);
                if (category != null) {
                    sendJsonResponse(resp, Result.success(category));
                } else {
                    sendJsonResponse(resp, Result.error("分类不存在"));
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的分类ID"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BookCategory category = gson.fromJson(readRequestBody(req), BookCategory.class);

        if (categoryDAO.addCategory(category)) {
            sendJsonResponse(resp, Result.success());
        } else {
            sendJsonResponse(resp, Result.error("添加分类失败"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BookCategory category = gson.fromJson(readRequestBody(req), BookCategory.class);

        if (categoryDAO.updateCategory(category)) {
            sendJsonResponse(resp, Result.success());
        } else {
            sendJsonResponse(resp, Result.error("更新分类失败"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                if (categoryDAO.deleteCategory(id)) {
                    sendJsonResponse(resp, Result.success());
                } else {
                    sendJsonResponse(resp, Result.error("删除分类失败"));
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的分类ID"));
            }
        } else {
            sendJsonResponse(resp, Result.error("请提供分类ID"));
        }
    }
}
