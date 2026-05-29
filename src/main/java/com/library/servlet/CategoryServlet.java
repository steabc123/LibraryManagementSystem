package com.library.servlet;

import com.google.gson.Gson;
import com.library.common.Result;
import com.library.dao.CategoryDAO;
import com.library.entity.BookCategory;
import com.library.util.GsonConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/categories/*")
public class CategoryServlet extends HttpServlet {
    private CategoryDAO categoryDAO = new CategoryDAO();
    private Gson gson = GsonConfig.getGson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<BookCategory> categories = categoryDAO.getAllCategories();
            resp.getWriter().write(gson.toJson(Result.success(categories)));
        } else {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                BookCategory category = categoryDAO.getCategoryById(id);
                if (category != null) {
                    resp.getWriter().write(gson.toJson(Result.success(category)));
                } else {
                    resp.getWriter().write(gson.toJson(Result.error("分类不存在")));
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write(gson.toJson(Result.error("无效的分类ID")));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        BookCategory category = gson.fromJson(sb.toString(), BookCategory.class);

        if (categoryDAO.addCategory(category)) {
            resp.getWriter().write(gson.toJson(Result.success()));
        } else {
            resp.getWriter().write(gson.toJson(Result.error("添加分类失败")));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        BookCategory category = gson.fromJson(sb.toString(), BookCategory.class);

        if (categoryDAO.updateCategory(category)) {
            resp.getWriter().write(gson.toJson(Result.success()));
        } else {
            resp.getWriter().write(gson.toJson(Result.error("更新分类失败")));
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
                    resp.getWriter().write(gson.toJson(Result.success()));
                } else {
                    resp.getWriter().write(gson.toJson(Result.error("删除分类失败")));
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write(gson.toJson(Result.error("无效的分类ID")));
            }
        } else {
            resp.getWriter().write(gson.toJson(Result.error("请提供分类ID")));
        }
    }
}