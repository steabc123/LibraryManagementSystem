package com.library.entity;

import java.time.LocalDateTime;

public class BorrowRecord {
    private Long recordId;
    private Integer userId;
    private Integer bookId;
    private LocalDateTime borrowTime;
    private LocalDateTime returnTime;
    private Integer borrowStatus;

    public BorrowRecord() {
    }

    public BorrowRecord(Long recordId, Integer userId, Integer bookId, LocalDateTime borrowTime,
                       LocalDateTime returnTime, Integer borrowStatus) {
        this.recordId = recordId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
        this.borrowStatus = borrowStatus;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
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

    public LocalDateTime getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(LocalDateTime borrowTime) {
        this.borrowTime = borrowTime;
    }

    public LocalDateTime getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }

    public Integer getBorrowStatus() {
        return borrowStatus;
    }

    public void setBorrowStatus(Integer borrowStatus) {
        this.borrowStatus = borrowStatus;
    }
}
