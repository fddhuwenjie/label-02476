package com.yuxuanlei.controller;

import com.yuxuanlei.common.Result;
import com.yuxuanlei.dto.BorrowRequestDTO;
import com.yuxuanlei.dto.ReturnRequestDTO;
import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.web.service.BorrowWebService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class BorrowController {

    @Autowired
    private BorrowWebService borrowWebService;

    @PostMapping("/borrow")
    public Result<BookBorrow> borrowBook(@Valid @RequestBody BorrowRequestDTO dto) {
        BookBorrow borrow = borrowWebService.borrowBook(dto.getStudentId(), dto.getBookId());
        return Result.success(borrow);
    }

    @PostMapping("/borrow/return")
    public Result<BookBorrow> returnBook(@Valid @RequestBody ReturnRequestDTO dto) {
        BookBorrow borrow = borrowWebService.returnBook(dto.getBorrowId());
        return Result.success(borrow);
    }

    @GetMapping("/borrow")
    public Result<List<BookBorrow>> listBorrows(
            @RequestParam(required = false) @Positive(message = "学生ID必须为正整数") Long studentId) {
        if (studentId != null) {
            List<BookBorrow> borrows = borrowWebService.getStudentBorrows(studentId);
            return Result.success(borrows);
        }
        return Result.success(null);
    }

    @GetMapping("/students/{id}/borrows")
    public Result<List<BookBorrow>> getStudentBorrows(
            @PathVariable @NotNull(message = "学生ID不能为空") @Positive(message = "学生ID必须为正整数") Long id) {
        List<BookBorrow> borrows = borrowWebService.getStudentBorrows(id);
        return Result.success(borrows);
    }
}
