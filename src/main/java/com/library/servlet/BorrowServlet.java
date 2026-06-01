package com.library.servlet;

import com.library.common.Result;
import com.library.dao.BorrowDAO;
import com.library.entity.BorrowRecord;
import com.library.entity.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/borrows/*")
public class BorrowServlet extends BaseServlet {
    private BorrowDAO borrowDAO = new BorrowDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkLogin(req, resp)) {
            return;
        }

        String pathInfo = req.getPathInfo();
        UserInfo currentUser = getCurrentUser(req);
        
        if ("/my".equals(pathInfo)) {
            List<BorrowRecord> records = borrowDAO.getBorrowRecordsByUserId(currentUser.getId());
            sendJsonResponse(resp, Result.success(records));
        } else if ("/current".equals(pathInfo)) {
            List<BorrowRecord> records = borrowDAO.getCurrentBorrows();
            sendJsonResponse(resp, Result.success(records));
        } else {
            List<BorrowRecord> records = borrowDAO.getAllBorrowRecords();
            sendJsonResponse(resp, Result.success(records));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkLogin(req, resp)) {
            return;
        }

        Map<String, Object> params = gson.fromJson(readRequestBody(req), Map.class);
        String action = (String) params.get("action");
        UserInfo currentUser = getCurrentUser(req);

        if ("borrow".equals(action)) {
            Integer bookId = ((Number) params.get("bookId")).intValue();

            if (borrowDAO.borrowBook(currentUser.getId(), bookId)) {
                sendJsonResponse(resp, Result.success());
            } else {
                sendJsonResponse(resp, Result.error("借阅失败，可能库存不足"));
            }
        } else if ("return".equals(action)) {
            Long recordId = ((Number) params.get("recordId")).longValue();

            if (borrowDAO.returnBook(recordId)) {
                sendJsonResponse(resp, Result.success());
            } else {
                sendJsonResponse(resp, Result.error("归还失败"));
            }
        }
    }
}
