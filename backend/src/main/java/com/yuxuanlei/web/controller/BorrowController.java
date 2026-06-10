package com.yuxuanlei.web.controller;

import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.web.common.Result;
import com.yuxuanlei.web.dto.BorrowRequest;
import com.yuxuanlei.web.service.BorrowWebService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 借阅管理 REST 接口
 */
@RestController
@Validated
public class BorrowController {

    private final BorrowWebService borrowService;

    public BorrowController(BorrowWebService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 借书
     */
    @PostMapping("/api/borrow")
    public Result<BookBorrow> borrow(@Valid @RequestBody BorrowRequest request) {
        return Result.success(borrowService.borrow(request));
    }

    /**
     * 还书
     */
    @GetMapping("/api/borrow")
    public Result<BookBorrow> returnBook(
            @RequestParam @Positive(message = "借阅记录ID必须是正整数") Long borrowId) {
        return Result.success(borrowService.returnBook(borrowId));
    }

    /**
     * 查询学生的借阅记录
     */
    @GetMapping("/api/students/{id}/borrows")
    public Result<List<BookBorrow>> listByStudent(
            @PathVariable @Positive(message = "学生ID必须是正整数") Long id) {
        return Result.success(borrowService.getBorrowsByStudent(id));
    }
}
