package com.library.dao;

import com.library.entity.BorrowRecord;
import com.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    public boolean borrowBook(Integer userId, Integer bookId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String checkStockSql = "SELECT stock FROM book_info WHERE id = ?";
            int stock = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(checkStockSql)) {
                pstmt.setInt(1, bookId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        stock = rs.getInt("stock");
                    } else {
                        return false;
                    }
                }
            }

            if (stock <= 0) {
                return false;
            }

            String updateStockSql = "UPDATE book_info SET stock = stock - 1 WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateStockSql)) {
                pstmt.setInt(1, bookId);
                pstmt.executeUpdate();
            }

            String insertBorrowSql = "INSERT INTO borrow_record (user_id, book_id, borrow_time, borrow_status) VALUES (?, ?, ?, 0)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertBorrowSql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, bookId);
                pstmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.executeUpdate();
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
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean returnBook(Long recordId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String getRecordSql = "SELECT book_id FROM borrow_record WHERE record_id = ? AND borrow_status = 0";
            Integer bookId = null;
            try (PreparedStatement pstmt = conn.prepareStatement(getRecordSql)) {
                pstmt.setLong(1, recordId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getInt("book_id");
                    } else {
                        return false;
                    }
                }
            }

            String updateBorrowSql = "UPDATE borrow_record SET return_time = ?, borrow_status = 1 WHERE record_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBorrowSql)) {
                pstmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.setLong(2, recordId);
                pstmt.executeUpdate();
            }

            String updateStockSql = "UPDATE book_info SET stock = stock + 1 WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateStockSql)) {
                pstmt.setInt(1, bookId);
                pstmt.executeUpdate();
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
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<BorrowRecord> getBorrowRecordsByUserId(Integer userId) {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_record WHERE user_id = ? ORDER BY borrow_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord record = new BorrowRecord();
                    record.setRecordId(rs.getLong("record_id"));
                    record.setUserId(rs.getInt("user_id"));
                    record.setBookId(rs.getInt("book_id"));
                    record.setBorrowTime(DBUtil.convertToBeijingTime(rs.getTimestamp("borrow_time")));

                    Timestamp returnTime = rs.getTimestamp("return_time");
                    if (returnTime != null) {
                        record.setReturnTime(DBUtil.convertToBeijingTime(returnTime));
                    }

                    record.setBorrowStatus(rs.getInt("borrow_status"));
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_record ORDER BY borrow_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setRecordId(rs.getLong("record_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBorrowTime(DBUtil.convertToBeijingTime(rs.getTimestamp("borrow_time")));

                Timestamp returnTime = rs.getTimestamp("return_time");
                if (returnTime != null) {
                    record.setReturnTime(DBUtil.convertToBeijingTime(returnTime));
                }

                record.setBorrowStatus(rs.getInt("borrow_status"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public List<BorrowRecord> getCurrentBorrows() {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_record WHERE borrow_status = 0 ORDER BY borrow_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setRecordId(rs.getLong("record_id"));
                record.setUserId(rs.getInt("user_id"));
                record.setBookId(rs.getInt("book_id"));
                record.setBorrowTime(DBUtil.convertToBeijingTime(rs.getTimestamp("borrow_time")));
                record.setBorrowStatus(rs.getInt("borrow_status"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }
}

