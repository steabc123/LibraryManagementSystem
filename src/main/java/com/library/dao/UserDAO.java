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
        if (user.getId() != null && !user.getId().equals(user.getOriginalId())) {
            UserInfo existingUser = getUserById(user.getId());
            if (existingUser != null) {
                System.out.println("用户ID " + user.getId() + " 已被其他用户使用");
                return false;
            }
        }
        
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            UserInfo existingUser = getUserByUsername(user.getUsername());
            if (existingUser != null && !existingUser.getId().equals(user.getOriginalId())) {
                System.out.println("用户名 " + user.getUsername() + " 已被其他用户使用");
                return false;
            }
        }
        
        String sql;
        if (user.getId() != null && !user.getId().equals(user.getOriginalId())) {
            sql = "UPDATE user_info SET id=?, username=?, email=?, phone=?, role=?, status=? WHERE id=?";
        } else {
            sql = "UPDATE user_info SET username=?, email=?, phone=?, role=?, status=? WHERE id=?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            
            if (user.getId() != null && !user.getId().equals(user.getOriginalId())) {
                pstmt.setInt(paramIndex++, user.getId());
            }
            
            pstmt.setString(paramIndex++, user.getUsername());
            pstmt.setString(paramIndex++, user.getEmail());
            pstmt.setString(paramIndex++, user.getPhone());
            pstmt.setString(paramIndex++, user.getRole());
            
            if (user.getStatus() != null) {
                pstmt.setInt(paramIndex++, user.getStatus());
            } else {
                pstmt.setNull(paramIndex++, java.sql.Types.INTEGER);
            }
            
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

    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        UserInfo user = getUserById(userId);
        if (user == null) {
            return false;
        }
        
        if (!oldPassword.equals(user.getPassword())) {
            return false;
        }
        
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

