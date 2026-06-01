package com.library.servlet;

import com.google.gson.Gson;
import com.library.common.Result;
import com.library.entity.UserInfo;
import com.library.util.GsonConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Servlet基类，提供公共方法
 */
public abstract class BaseServlet extends jakarta.servlet.http.HttpServlet {
    protected Gson gson = GsonConfig.getGson();

    /**
     * 读取请求体内容
     */
    protected String readRequestBody(HttpServletRequest req) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * 检查用户是否登录
     */
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

    /**
     * 检查是否为管理员
     */
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

    /**
     * 获取当前登录用户
     */
    protected UserInfo getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (UserInfo) session.getAttribute("currentUser");
        }
        return null;
    }

    /**
     * 发送JSON响应
     */
    protected void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(data));
    }
}
