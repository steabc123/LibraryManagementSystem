package com.library.dao;

import com.library.entity.OrderInfo;
import com.library.entity.OrderItem;
import com.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean createOrder(OrderInfo order, List<OrderItem> items) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            String orderSql = "INSERT INTO orders (user_id, total_amount, order_status, create_time) VALUES (?, ?, ?, ?)";
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setBigDecimal(2, order.getTotalAmount());
            orderStmt.setInt(3, order.getOrderStatus());
            orderStmt.setTimestamp(4, Timestamp.valueOf(order.getCreateTime()));
            orderStmt.executeUpdate();
            
            rs = orderStmt.getGeneratedKeys();
            Long orderId = null;
            if (rs.next()) {
                orderId = rs.getLong(1);
            }
            
            if (orderId == null) {
                conn.rollback();
                return false;
            }
            
            String itemSql = "INSERT INTO order_item (order_id, book_id, book_name, author, price, quantity) VALUES (?, ?, ?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);
            
            for (OrderItem item : items) {
                itemStmt.setLong(1, orderId);
                itemStmt.setInt(2, item.getBookId());
                itemStmt.setString(3, item.getBookName());
                itemStmt.setString(4, item.getAuthor());
                itemStmt.setBigDecimal(5, item.getPrice());
                itemStmt.setInt(6, item.getQuantity());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();
            
            conn.commit();
            order.setId(orderId);
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (itemStmt != null) {
                try { itemStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (orderStmt != null) {
                try { orderStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true);
                    conn.close(); 
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public List<OrderInfo> getOrdersByUserId(Integer userId) {
        List<OrderInfo> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username FROM orders o LEFT JOIN user_info u ON o.user_id = u.id WHERE o.user_id = ? ORDER BY o.create_time DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderInfo order = new OrderInfo();
                    order.setId(rs.getLong("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setUsername(rs.getString("username"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setOrderStatus(rs.getInt("order_status"));
                    order.setCreateTime(DBUtil.convertToBeijingTime(rs.getTimestamp("create_time")));
                    order.setPayTime(DBUtil.convertToBeijingTime(rs.getTimestamp("pay_time")));
                    order.setShipTime(DBUtil.convertToBeijingTime(rs.getTimestamp("ship_time")));
                    order.setReceiveTime(DBUtil.convertToBeijingTime(rs.getTimestamp("receive_time")));
                    
                    order.setItems(getOrderItems(order.getId()));
                    
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }

    public List<OrderInfo> getAllOrders() {
        List<OrderInfo> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username FROM orders o LEFT JOIN user_info u ON o.user_id = u.id ORDER BY o.create_time DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderInfo order = new OrderInfo();
                    order.setId(rs.getLong("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setUsername(rs.getString("username"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setOrderStatus(rs.getInt("order_status"));
                    order.setCreateTime(DBUtil.convertToBeijingTime(rs.getTimestamp("create_time")));
                    order.setPayTime(DBUtil.convertToBeijingTime(rs.getTimestamp("pay_time")));
                    order.setShipTime(DBUtil.convertToBeijingTime(rs.getTimestamp("ship_time")));
                    order.setReceiveTime(DBUtil.convertToBeijingTime(rs.getTimestamp("receive_time")));
                    
                    order.setItems(getOrderItems(order.getId()));
                    
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }

    public OrderInfo getOrderById(Long orderId) {
        String sql = "SELECT o.*, u.username FROM orders o LEFT JOIN user_info u ON o.user_id = u.id WHERE o.id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    OrderInfo order = new OrderInfo();
                    order.setId(rs.getLong("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setUsername(rs.getString("username"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setOrderStatus(rs.getInt("order_status"));
                    order.setCreateTime(DBUtil.convertToBeijingTime(rs.getTimestamp("create_time")));
                    order.setPayTime(DBUtil.convertToBeijingTime(rs.getTimestamp("pay_time")));
                    order.setShipTime(DBUtil.convertToBeijingTime(rs.getTimestamp("ship_time")));
                    order.setReceiveTime(DBUtil.convertToBeijingTime(rs.getTimestamp("receive_time")));
                    
                    order.setItems(getOrderItems(order.getId()));
                    
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private List<OrderItem> getOrderItems(Long orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, orderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setBookId(rs.getInt("book_id"));
                    item.setBookName(rs.getString("book_name"));
                    item.setAuthor(rs.getString("author"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setQuantity(rs.getInt("quantity"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }

    public boolean updateOrderStatus(Long orderId, Integer status, String timeField) {
        String sql = "UPDATE orders SET order_status = ?";
        List<Object> params = new ArrayList<>();
        params.add(status);
        
        if ("pay_time".equals(timeField)) {
            sql += ", pay_time = ?";
            params.add(Timestamp.valueOf(java.time.LocalDateTime.now()));
        } else if ("ship_time".equals(timeField)) {
            sql += ", ship_time = ?";
            params.add(Timestamp.valueOf(java.time.LocalDateTime.now()));
        } else if ("receive_time".equals(timeField)) {
            sql += ", receive_time = ?";
            params.add(Timestamp.valueOf(java.time.LocalDateTime.now()));
        }
        
        sql += " WHERE id = ?";
        params.add(orderId);
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOrder(Long orderId) {
        Connection conn = null;
        PreparedStatement itemStmt = null;
        PreparedStatement orderStmt = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            String itemSql = "DELETE FROM order_item WHERE order_id = ?";
            itemStmt = conn.prepareStatement(itemSql);
            itemStmt.setLong(1, orderId);
            itemStmt.executeUpdate();
            
            String orderSql = "DELETE FROM orders WHERE id = ?";
            orderStmt = conn.prepareStatement(orderSql);
            orderStmt.setLong(1, orderId);
            int result = orderStmt.executeUpdate();
            
            conn.commit();
            return result > 0;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (itemStmt != null) {
                try { itemStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (orderStmt != null) {
                try { orderStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true);
                    conn.close(); 
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean payOrder(Long orderId) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            String checkSql = "SELECT order_status FROM orders WHERE id = ?";
            orderStmt = conn.prepareStatement(checkSql);
            orderStmt.setLong(1, orderId);
            rs = orderStmt.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            
            int status = rs.getInt("order_status");
            if (status != 0) {
                conn.rollback();
                return false;
            }
            rs.close();
            orderStmt.close();
            
            List<OrderItem> items = getOrderItems(orderId);
            if (items == null || items.isEmpty()) {
                conn.rollback();
                return false;
            }
            
            List<int[]> bookQuantities = new ArrayList<>();
            for (OrderItem item : items) {
                bookQuantities.add(new int[]{item.getBookId(), item.getQuantity()});
            }
            
            String stockSql = "UPDATE book_info SET stock = stock - ? WHERE id = ? AND stock >= ?";
            PreparedStatement stockStmt = conn.prepareStatement(stockSql);
            
            for (int[] bq : bookQuantities) {
                int bookId = bq[0];
                int quantity = bq[1];
                stockStmt.setInt(1, quantity);
                stockStmt.setInt(2, bookId);
                stockStmt.setInt(3, quantity);
                stockStmt.addBatch();
            }
            
            int[] results = stockStmt.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    conn.rollback();
                    stockStmt.close();
                    return false;
                }
            }
            stockStmt.close();
            
            String updateSql = "UPDATE orders SET order_status = ?, pay_time = ? WHERE id = ?";
            orderStmt = conn.prepareStatement(updateSql);
            orderStmt.setInt(1, 1);
            orderStmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            orderStmt.setLong(3, orderId);
            
            int updateResult = orderStmt.executeUpdate();
            if (updateResult <= 0) {
                conn.rollback();
                return false;
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (itemStmt != null) {
                try { itemStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (orderStmt != null) {
                try { orderStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true);
                    conn.close(); 
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
