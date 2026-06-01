package com.library.servlet;

import com.library.common.Result;
import com.library.dao.UserDAO;
import com.library.entity.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/users/*")
public class UserServlet extends BaseServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkLogin(req, resp)) {
            return;
        }

        String pathInfo = req.getPathInfo();
        
        // 获取所有用户列表
        if ("/all".equals(pathInfo)) {
            List<UserInfo> users = userDAO.getAllUsers();
            users.forEach(user -> user.setPassword(null));
            sendJsonResponse(resp, Result.success(users));
            return;
        }
        
        // 获取指定ID的用户
        if (pathInfo != null && !pathInfo.equals("/")) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                UserInfo user = userDAO.getUserById(id);
                if (user != null) {
                    user.setPassword(null);
                    sendJsonResponse(resp, Result.success(user));
                } else {
                    sendJsonResponse(resp, Result.error("用户不存在"));
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的用户ID"));
            }
            return;
        }
        
        // 获取当前登录用户信息
        UserInfo currentUser = getCurrentUser(req);
        if (currentUser == null) {
            sendJsonResponse(resp, Result.error(401, "未登录"));
            return;
        }
        currentUser.setPassword(null);
        sendJsonResponse(resp, Result.success(currentUser));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Map<String, String> params = gson.fromJson(readRequestBody(req), Map.class);
        String action = req.getParameter("action");

        if ("login".equals(action)) {
            String username = params.get("username");
            String password = params.get("password");

            UserInfo user = userDAO.login(username, password);
            if (user != null) {
                HttpSession session = req.getSession();
                session.setAttribute("currentUser", user);

                Map<String, Object> data = new HashMap<>();
                data.put("userId", user.getId());
                data.put("username", user.getUsername());
                data.put("role", user.getRole());

                sendJsonResponse(resp, Result.success(data));
            } else {
                sendJsonResponse(resp, Result.error("用户名或密码错误"));
            }
        } else if ("register".equals(action)) {
            String username = params.get("username");
            String password = params.get("password");

            UserInfo newUser = new UserInfo();
            newUser.setUsername(username);
            newUser.setPassword(password);

            if (userDAO.register(newUser)) {
                sendJsonResponse(resp, Result.success());
            } else {
                sendJsonResponse(resp, Result.error("注册失败"));
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkLogin(req, resp)) {
            return;
        }

        UserInfo user = gson.fromJson(readRequestBody(req), UserInfo.class);

        if (userDAO.updateUser(user)) {
            sendJsonResponse(resp, Result.success());
        } else {
            sendJsonResponse(resp, Result.error("更新用户失败"));
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
                int id = Integer.parseInt(pathInfo.substring(1));
                if (userDAO.deleteUser(id)) {
                    sendJsonResponse(resp, Result.success());
                } else {
                    sendJsonResponse(resp, Result.error("删除用户失败"));
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(resp, Result.error("无效的用户ID"));
            }
        } else {
            sendJsonResponse(resp, Result.error("请提供用户ID"));
        }
    }
}
