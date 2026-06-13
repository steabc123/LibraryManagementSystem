package com.library.dao;

import com.library.entity.UserInfo;
import com.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public UserInfo login(String username, String password) {
        String sql = "SELECT * FROM user_info WHERE username = ? AND password = ? AND status = 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserInfo user = new UserInfo();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getInt("status"));
                    user.setCreateTime(DBUtil.convertToBeijingTime(rs.getTimestamp("create_time")));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean register(UserInfo user) {
        String sql = "INSERT INTO user_info (username, password, email, phone, role) VALUES (?, ?, ?, ?, 'user')";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(UserInfo user) {
        String sql = "INSERT INTO user_info (username, password, email, phone, role, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getRole());
            pstmt.setInt(6, user.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public UserInfo getUserById(int id) {
        String sql = "SELECT * FROM user_info WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserInfo user = new UserInfo();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getInt("status"));
                    user.setCreateTime(DBUtil.convertToBeijingTime(rs.getTimestamp("create_time")));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<UserInfo> getAllUsers() {
        List<UserInfo> users = new ArrayList<>();
        String sql = "SELECT id, username, email, phone, role, status, create_time FROM user_info ORDER BY create_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UserInfo user = new UserInfo();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getInt("status"));
                user.setCreateTime(DBUtil.convertToBeijingTime(rs.getTimestamp("create_time")));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    public UserInfo getUserByUsername(String username) {
        String sql = "SELECT * FROM user_info WHERE username = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserInfo user = new UserInfo();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getInt("status"));
                    user.setCreateTime(DBUtil.convertToBeijingTime(rs.getTimestamp("create_time")));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateUser(UserInfo user) {
        // 如果提供了新的 ID，需要检查新 ID 是否已存在
        if (user.getId() != null && !user.getId().equals(user.getOriginalId())) {
            // 检查新 ID 是否已被使用
            UserInfo existingUser = getUserById(user.getId());
            if (existingUser != null) {
                System.out.println("用户ID " + user.getId() + " 已被其他用户使用");
                return false;
            }
        }
        
        // 如果提供了新的用户名，需要检查新用户名是否已存在
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            UserInfo existingUser = getUserByUsername(user.getUsername());
            if (existingUser != null && !existingUser.getId().equals(user.getOriginalId())) {
                System.out.println("用户名 " + user.getUsername() + " 已被其他用户使用");
                return false;
            }
        }
        
        String sql;
        // 如果 ID 发生变化，需要同时更新 ID
        if (user.getId() != null && !user.getId().equals(user.getOriginalId())) {
            sql = "UPDATE user_info SET id=?, username=?, email=?, phone=?, role=?, status=? WHERE id=?";
        } else {
            sql = "UPDATE user_info SET username=?, email=?, phone=?, role=?, status=? WHERE id=?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            
            // 如果 ID 变化，先设置新 ID
            if (user.getId() != null && !user.getId().equals(user.getOriginalId())) {
                pstmt.setInt(paramIndex++, user.getId());
            }
            
            // 设置用户名
            pstmt.setString(paramIndex++, user.getUsername());
            pstmt.setString(paramIndex++, user.getEmail());
            pstmt.setString(paramIndex++, user.getPhone());
            pstmt.setString(paramIndex++, user.getRole());
            
            // 处理 status 可能为 null 的情况
            if (user.getStatus() != null) {
                pstmt.setInt(paramIndex++, user.getStatus());
            } else {
                pstmt.setNull(paramIndex++, java.sql.Types.INTEGER);
            }
            
            // 最后设置原 ID（WHERE 条件）
            pstmt.setInt(paramIndex, user.getOriginalId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM user_info WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改用户密码（带旧密码验证）
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return true-成功，false-失败
     */
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        // 先验证旧密码是否正确
        UserInfo user = getUserById(userId);
        if (user == null) {
            return false;
        }
        
        // 简单明文比较（生产环境应该使用加密后的密码比较）
        if (!oldPassword.equals(user.getPassword())) {
            return false; // 旧密码错误
        }
        
        // 更新为新密码
        String sql = "UPDATE user_info SET password = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

