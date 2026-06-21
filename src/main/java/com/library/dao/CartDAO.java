package com.library.dao;

import com.library.entity.CartItem;
import com.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public boolean addToCart(Integer userId, Integer bookId, String bookName, String author, Double price, Integer quantity) {
        String checkSql = "SELECT id, quantity FROM cart_item WHERE user_id = ? AND book_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, bookId);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int existingId = rs.getInt("id");
                    int existingQuantity = rs.getInt("quantity");
                    int newQuantity = existingQuantity + quantity;
                    
                    String updateSql = "UPDATE cart_item SET quantity = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newQuantity);
                        updateStmt.setInt(2, existingId);
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    String insertSql = "INSERT INTO cart_item (user_id, book_id, book_name, author, price, quantity) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, bookId);
                        insertStmt.setString(3, bookName);
                        insertStmt.setString(4, author);
                        insertStmt.setDouble(5, price);
                        insertStmt.setInt(6, quantity);
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CartItem> getCartByUserId(Integer userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT c.*, b.publisher, b.isbn FROM cart_item c LEFT JOIN book_info b ON c.book_id = b.id WHERE c.user_id = ? ORDER BY c.create_time DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setBookId(rs.getInt("book_id"));
                    item.setBookName(rs.getString("book_name"));
                    item.setAuthor(rs.getString("author"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCreateTime(DBUtil.convertToBeijingTime(rs.getTimestamp("create_time")));
                    item.setPublisher(rs.getString("publisher"));
                    item.setIsbn(rs.getString("isbn"));
                    cartItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cartItems;
    }

    public boolean updateCartItemQuantity(Integer cartItemId, Integer quantity) {
        String sql = "UPDATE  cart_item SET quantity = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, cartItemId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCartItem(Integer cartItemId) {
        String sql = "DELETE FROM cart_item WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cartItemId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearCart(Integer userId) {
        String sql = "DELETE FROM cart_item WHERE user_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double calculateTotalAmount(Integer userId) {
        String sql = "SELECT SUM(price * quantity) as total FROM cart_item WHERE user_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble("total");
                    return rs.wasNull() ? 0.0 : total;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
}
