package com.yuxuanlei.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReturnRequestDTO {

    @NotNull(message = "借阅记录ID不能为空")
    @Positive(message = "借阅记录ID必须为正整数")
    private Long borrowId;

    public Long getBorrowId() { return borrowId; }
    public void setBorrowId(Long borrowId) { this.borrowId = borrowId; }
}
