package com.yuxuanlei.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 借书请求 DTO
 */
public class BorrowRequest {

    @NotNull(message = "学生ID不能为空")
    @Positive(message = "学生ID必须是正整数")
    private Long studentId;

    @NotNull(message = "图书ID不能为空")
    @Positive(message = "图书ID必须是正整数")
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
