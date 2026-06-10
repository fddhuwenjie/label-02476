package com.yuxuanlei.dto;

import jakarta.validation.constraints.NotNull;

public class BorrowBookRequest {

    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
