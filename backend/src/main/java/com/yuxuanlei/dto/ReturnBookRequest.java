package com.yuxuanlei.dto;

import jakarta.validation.constraints.NotNull;

public class ReturnBookRequest {

    @NotNull(message = "借阅记录ID不能为空")
    private Long borrowId;

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }
}
