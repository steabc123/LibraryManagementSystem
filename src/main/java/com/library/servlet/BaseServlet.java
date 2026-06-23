package com.library.servlet;

import com.google.gson.Gson;
import com.library.common.Result;
import com.library.entity.UserInfo;
import com.library.util.GsonConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected Gson gson = GsonConfig.getGson();

    protected String readRequestBody(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    protected boolean checkLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(Result.error(401, "未登录")));
            return false;
        }
        return true;
    }

    protected boolean checkAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(Result.error(401, "未登录")));
            return false;
        }

        UserInfo user = (UserInfo) session.getAttribute("currentUser");
        if (!"admin".equals(user.getRole())) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(gson.toJson(Result.error(403, "权限不足")));
            return false;
        }
        return true;
    }

    protected UserInfo getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (UserInfo) session.getAttribute("currentUser");
        }
        return null;
    }

    protected void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(data));
    }
}
