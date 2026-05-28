package com.library.servlet;

import com.google.gson.Gson;
import com.library.common.Result;
import com.library.dao.BookDAO;
import com.library.entity.BookInfo;
import com.library.util.GsonConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/books/*")
public class BookServlet extends HttpServlet {
    private BookDAO bookDAO = new BookDAO();
    private Gson gson = GsonConfig.getGson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String keyword = req.getParameter("keyword");
            if (keyword != null && !keyword.trim().isEmpty()) {
                List<BookInfo> books = bookDAO.searchBooks(keyword.trim());
                resp.getWriter().write(gson.toJson(Result.success(books)));
            } else {
                List<BookInfo> books = bookDAO.getAllBooks();
                resp.getWriter().write(gson.toJson(Result.success(books)));
            }
        } else {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                BookInfo book = bookDAO.getBookById(id);
                if (book != null) {
                    resp.getWriter().write(gson.toJson(Result.success(book)));
                } else {
                    resp.getWriter().write(gson.toJson(Result.error("图书不存在")));
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write(gson.toJson(Result.error("无效的图书ID")));
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

        BookInfo book = gson.fromJson(sb.toString(), BookInfo.class);

        if (bookDAO.addBook(book)) {
            resp.getWriter().write(gson.toJson(Result.success()));
        } else {
            resp.getWriter().write(gson.toJson(Result.error("添加图书失败")));
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

        BookInfo book = gson.fromJson(sb.toString(), BookInfo.class);

        if (bookDAO.updateBook(book)) {
            resp.getWriter().write(gson.toJson(Result.success()));
        } else {
            resp.getWriter().write(gson.toJson(Result.error("更新图书失败")));
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
                if (bookDAO.deleteBook(id)) {
                    resp.getWriter().write(gson.toJson(Result.success()));
                } else {
                    resp.getWriter().write(gson.toJson(Result.error("删除图书失败")));
                }
            } catch (NumberFormatException e) {
                resp.getWriter().write(gson.toJson(Result.error("无效的图书ID")));
            }
        } else {
            resp.getWriter().write(gson.toJson(Result.error("请提供图书ID")));
        }
    }
}

