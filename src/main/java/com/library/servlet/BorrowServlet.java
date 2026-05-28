package com.library.servlet;

import com.google.gson.Gson;
import com.library.common.Result;
import com.library.dao.BorrowDAO;
import com.library.entity.BorrowRecord;
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

@WebServlet("/api/borrows/*")
public class BorrowServlet extends HttpServlet {
    private BorrowDAO borrowDAO = new BorrowDAO();
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

        String pathInfo = req.getPathInfo();
        if ("/my".equals(pathInfo)) {
            Integer userId = ((com.library.entity.UserInfo) session.getAttribute("currentUser")).getId();
            List<BorrowRecord> records = borrowDAO.getBorrowRecordsByUserId(userId);
            resp.getWriter().write(gson.toJson(Result.success(records)));
        } else if ("/current".equals(pathInfo)) {
            List<BorrowRecord> records = borrowDAO.getCurrentBorrows();
            resp.getWriter().write(gson.toJson(Result.success(records)));
        } else {
            List<BorrowRecord> records = borrowDAO.getAllBorrowRecords();
            resp.getWriter().write(gson.toJson(Result.success(records)));
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

        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        Map<String, Object> params = gson.fromJson(sb.toString(), Map.class);
        String action = (String) params.get("action");

        if ("borrow".equals(action)) {
            Integer userId = ((com.library.entity.UserInfo) session.getAttribute("currentUser")).getId();
            Integer bookId = ((Number) params.get("bookId")).intValue();

            if (borrowDAO.borrowBook(userId, bookId)) {
                resp.getWriter().write(gson.toJson(Result.success()));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("借阅失败，可能库存不足")));
            }
        } else if ("return".equals(action)) {
            Long recordId = ((Number) params.get("recordId")).longValue();

            if (borrowDAO.returnBook(recordId)) {
                resp.getWriter().write(gson.toJson(Result.success()));
            } else {
                resp.getWriter().write(gson.toJson(Result.error("归还失败")));
            }
        }
    }
}
