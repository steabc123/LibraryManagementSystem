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
        
        if ("/all".equals(pathInfo)) {
            if (!checkAdmin(req, resp)) {
                return;
            }
            List<UserInfo> users = userDAO.getAllUsers();
            users.forEach(user -> user.setPassword(null));
            sendJsonResponse(resp, Result.success(users));
            return;
        }
        
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
        } else if ("addUser".equals(action)) {
            if (!checkAdmin(req, resp)) {
                return;
            }
            
            String username = params.get("username");
            String password = params.get("password");
            Object roleObj = params.get("role");
            Object statusObj = params.get("status");
            
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                sendJsonResponse(resp, Result.error("用户名和密码不能为空"));
                return;
            }
            
            UserInfo newUser = new UserInfo();
            newUser.setUsername(username.trim());
            newUser.setPassword(password);
            newUser.setRole(roleObj != null ? roleObj.toString() : "user");
            
            if (statusObj != null) {
                if (statusObj instanceof Number) {
                    newUser.setStatus(((Number) statusObj).intValue());
                } else {
                    newUser.setStatus(Integer.parseInt(statusObj.toString()));
                }
            } else {
                newUser.setStatus(1);
            }

            if (userDAO.addUser(newUser)) {
                sendJsonResponse(resp, Result.success());
            } else {
                sendJsonResponse(resp, Result.error("添加用户失败"));
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

        String action = req.getParameter("action");
        
        if ("changePassword".equals(action)) {
            handleChangePassword(req, resp);
        } else {
            handleUpdateUser(req, resp);
        }
    }
    
    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserInfo currentUser = (UserInfo) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            sendJsonResponse(resp, Result.error("未找到当前登录用户"));
            return;
        }
        
        Map<String, Object> params = gson.fromJson(readRequestBody(req), Map.class);
        
        System.out.println("=== 修改密码请求参数 ===");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("========================");
        
        String oldPassword = (String) params.get("oldPassword");
        if (oldPassword == null) {
            oldPassword = (String) params.get("old_password");
        }
        
        String newPassword = (String) params.get("newPassword");
        if (newPassword == null) {
            newPassword = (String) params.get("new_password");
        }
        
        String confirmPassword = (String) params.get("confirmPassword");
        if (confirmPassword == null) {
            confirmPassword = (String) params.get("confirm_password");
        }
        if (confirmPassword == null) {
            confirmPassword = (String) params.get("confirmNewPassword");
        }
        if (confirmPassword == null) {
            confirmPassword = (String) params.get("confirm_new_password");
        }
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            sendJsonResponse(resp, Result.error("旧密码不能为空"));
            return;
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            sendJsonResponse(resp, Result.error("新密码不能为空"));
            return;
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            sendJsonResponse(resp, Result.error("确认密码不能为空"));
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            sendJsonResponse(resp, Result.error("两次输入的新密码不一致"));
            return;
        }
        
        if (newPassword.length() < 6) {
            sendJsonResponse(resp, Result.error("新密码长度不能少于6位"));
            return;
        }
        
        if (userDAO.changePassword(currentUser.getId(), oldPassword, newPassword)) {
            sendJsonResponse(resp, Result.success("密码修改成功"));
        } else {
            sendJsonResponse(resp, Result.error("旧密码错误或修改失败"));
        }
    }
    
    private void handleUpdateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserInfo user = gson.fromJson(readRequestBody(req), UserInfo.class);
        
        if (user.getOriginalId() == null && user.getId() != null) {
            user.setOriginalId(user.getId());
        }
        
        if (user.getOriginalId() == null) {
            sendJsonResponse(resp, Result.error("用户ID不能为空"));
            return;
        }
        
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            sendJsonResponse(resp, Result.error("用户名不能为空"));
            return;
        }
        
        if (user.getStatus() == null || user.getRole() == null) {
            UserInfo currentUser = userDAO.getUserById(user.getOriginalId());
            if (currentUser != null) {
                if (user.getStatus() == null) {
                    user.setStatus(currentUser.getStatus());
                }
                if (user.getRole() == null) {
                    user.setRole(currentUser.getRole());
                }
            }
        }
        
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!user.getEmail().matches(emailRegex)) {
                sendJsonResponse(resp, Result.error("邮箱格式不正确"));
                return;
            }
        }
        
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            String phoneRegex = "^1[3-9]\\d{9}$";
            if (!user.getPhone().matches(phoneRegex)) {
                sendJsonResponse(resp, Result.error("手机号格式不正确，应为11位数字"));
                return;
            }
        }

        if (userDAO.updateUser(user)) {
            UserInfo sessionUser = (UserInfo) req.getSession().getAttribute("currentUser");
            if (sessionUser != null && sessionUser.getId().equals(user.getOriginalId())) {
                UserInfo updatedUser = userDAO.getUserById(user.getId());
                req.getSession().setAttribute("currentUser", updatedUser);
            }
            sendJsonResponse(resp, Result.success());
        } else {
            sendJsonResponse(resp, Result.error("更新用户失败，可能是ID或用户名已被其他用户使用"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!checkAdmin(req, resp)) {
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
