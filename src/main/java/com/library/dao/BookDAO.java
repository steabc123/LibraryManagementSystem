package com.library.dao;

import com.library.entity.BookInfo;
import com.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<BookInfo> getAllBooks() {
        List<BookInfo> books = new ArrayList<>();
        String sql = "SELECT b.*, c.category_name FROM book_info b LEFT JOIN book_category c ON b.category_id = c.category_id ORDER BY b.create_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BookInfo book = new BookInfo();
                book.setId(rs.getInt("id"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPublisher(rs.getString("publisher"));
                book.setPrice(rs.getBigDecimal("price"));
                book.setStock(rs.getInt("stock"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setCategoryName(rs.getString("category_name"));
                book.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    public BookInfo getBookById(int id) {
        String sql = "SELECT b.*, c.category_name FROM book_info b LEFT JOIN book_category c ON b.category_id = c.category_id WHERE b.id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BookInfo book = new BookInfo();
                    book.setId(rs.getInt("id"));
                    book.setBookName(rs.getString("book_name"));
                    book.setAuthor(rs.getString("author"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setPublisher(rs.getString("publisher"));
                    book.setPrice(rs.getBigDecimal("price"));
                    book.setStock(rs.getInt("stock"));
                    book.setCategoryId(rs.getInt("category_id"));
                    book.setCategoryName(rs.getString("category_name"));
                    book.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                    return book;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addBook(BookInfo book) {
        String sql = "INSERT INTO book_info (book_name, author, isbn, publisher, price, stock, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getBookName());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getPublisher());
            pstmt.setBigDecimal(5, book.getPrice());
            pstmt.setInt(6, book.getStock());
            pstmt.setInt(7, book.getCategoryId() != null ? book.getCategoryId() : 1);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBook(BookInfo book) {
        String sql = "UPDATE book_info SET book_name=?, author=?, isbn=?, publisher=?, price=?, stock=?, category_id=? WHERE id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getBookName());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getPublisher());
            pstmt.setBigDecimal(5, book.getPrice());
            pstmt.setInt(6, book.getStock());
            pstmt.setInt(7, book.getCategoryId() != null ? book.getCategoryId() : 1);
            pstmt.setInt(8, book.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(int id) {
        String sql = "DELETE FROM book_info WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<BookInfo> searchBooks(String keyword) {
        List<BookInfo> books = new ArrayList<>();
        String sql = "SELECT b.*, c.category_name FROM book_info b LEFT JOIN book_category c ON b.category_id = c.category_id WHERE b.book_name LIKE ? OR b.author LIKE ? OR b.isbn LIKE ? ORDER BY b.create_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BookInfo book = new BookInfo();
                    book.setId(rs.getInt("id"));
                    book.setBookName(rs.getString("book_name"));
                    book.setAuthor(rs.getString("author"));
                    book.setIsbn(rs.getString("isbn"));
                    book.setPublisher(rs.getString("publisher"));
                    book.setPrice(rs.getBigDecimal("price"));
                    book.setStock(rs.getInt("stock"));
                    book.setCategoryId(rs.getInt("category_id"));
                    book.setCategoryName(rs.getString("category_name"));
                    book.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
}

