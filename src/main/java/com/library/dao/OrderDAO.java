package com.library.dao;

import com.library.entity.OrderInfo;
import com.library.entity.OrderItem;
import com.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // 创建订单（事务操作）
    public boolean createOrder(OrderInfo order, List<OrderItem> items) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            // 1. 插入订单主表
            String orderSql = "INSERT INTO orders (user_id, total_amount, order_status, create_time) VALUES (?, ?, ?, ?)";
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setBigDecimal(2, order.getTotalAmount());
            orderStmt.setInt(3, order.getOrderStatus());
            orderStmt.setTimestamp(4, Timestamp.valueOf(order.getCreateTime()));
            orderStmt.executeUpdate();
            
            // 获取生成的订单ID
            rs = orderStmt.getGeneratedKeys();
            Long orderId = null;
            if (rs.next()) {
                orderId = rs.getLong(1);
            }
            
            if (orderId == null) {
                conn.rollback();
                return false;
            }
            
            // 2. 插入订单项
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

    // 获取用户订单列表
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
                    
                    // 加载订单项
                    order.setItems(getOrderItems(order.getId()));
                    
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }

    // 获取所有订单（管理员）
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
                    
                    // 加载订单项
                    order.setItems(getOrderItems(order.getId()));
                    
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }

    // 获取订单详情
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
                    
                    // 加载订单项
                    order.setItems(getOrderItems(order.getId()));
                    
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // 获取订单项列表
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

    // 更新订单状态
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

    // 删除订单
    public boolean deleteOrder(Long orderId) {
        Connection conn = null;
        PreparedStatement itemStmt = null;
        PreparedStatement orderStmt = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            // 先删除订单项
            String itemSql = "DELETE FROM order_item WHERE order_id = ?";
            itemStmt = conn.prepareStatement(itemSql);
            itemStmt.setLong(1, orderId);
            itemStmt.executeUpdate();
            
            // 再删除订单
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

    // 支付订单并减少库存（事务操作）
    public boolean payOrder(Long orderId) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            // 1. 检查订单状态
            String checkSql = "SELECT order_status FROM orders WHERE id = ?";
            orderStmt = conn.prepareStatement(checkSql);
            orderStmt.setLong(1, orderId);
            rs = orderStmt.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return false; // 订单不存在
            }
            
            int status = rs.getInt("order_status");
            if (status != 0) {
                conn.rollback();
                return false; // 订单状态不正确
            }
            rs.close();
            orderStmt.close();
            
            // 2. 获取订单项列表
            List<OrderItem> items = getOrderItems(orderId);
            if (items == null || items.isEmpty()) {
                conn.rollback();
                return false;
            }
            
            // 3. 批量减少库存
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
            // 检查是否所有库存都成功减少
            for (int result : results) {
                if (result <= 0) {
                    conn.rollback();
                    stockStmt.close();
                    return false; // 库存不足
                }
            }
            stockStmt.close();
            
            // 4. 更新订单状态为已支付
            String updateSql = "UPDATE orders SET order_status = ?, pay_time = ? WHERE id = ?";
            orderStmt = conn.prepareStatement(updateSql);
            orderStmt.setInt(1, 1); // 状态改为已支付
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
