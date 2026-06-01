package com.library.servlet;

import com.library.common.Result;
import com.library.dao.BookDAO;
import com.library.entity.BookInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/books/*")
public class BookServlet extends BaseServlet {
    private BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String keyword = req.getParameter("keyword");
            if (keyword != null && !keyword.trim().isEmpty()) {
                List<BookInfo> books = bookDAO.searchBooks(keyword.trim());
                sendJsonResponse(resp, Result.success(books));
            } else {
                List<BookInfo> books = bookDAO.getAllBooks();
                sendJsonResponse(resp, Result.success(books));
            }
        } else {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                BookInfo book = bookDAO.getBookById(id);
                if (book != null) {
                    sendJsonResponse(resp, Result.success(book));
                } else {
                    sendJsonResponse(resp, Result.error("图书不存在"));
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的图书ID"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BookInfo book = gson.fromJson(readRequestBody(req), BookInfo.class);

        if (bookDAO.addBook(book)) {
            sendJsonResponse(resp, Result.success());
        } else {
            sendJsonResponse(resp, Result.error("添加图书失败"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        BookInfo book = gson.fromJson(readRequestBody(req), BookInfo.class);

        if (bookDAO.updateBook(book)) {
            sendJsonResponse(resp, Result.success());
        } else {
            sendJsonResponse(resp, Result.error("更新图书失败"));
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
                    sendJsonResponse(resp, Result.success());
                } else {
                    sendJsonResponse(resp, Result.error("删除图书失败"));
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的图书ID"));
            }
        } else {
            sendJsonResponse(resp, Result.error("请提供图书ID"));
        }
    }
}
