package com.library.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItem {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private String bookName;
    private String author;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createTime;
    private String publisher;
    private String isbn;

    public CartItem() {
    }

    public CartItem(Integer userId, Integer bookId, String bookName, String author, BigDecimal price, Integer quantity, String publisher, String isbn) {
        this.userId = userId;
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.publisher = publisher;
        this.isbn = isbn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getTotalPrice() {
        if (price != null && quantity != null) {
            return price.multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
}
