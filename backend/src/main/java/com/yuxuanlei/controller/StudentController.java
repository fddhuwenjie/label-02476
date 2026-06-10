package com.yuxuanlei.controller;

import com.yuxuanlei.common.Result;
import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.service.BookBorrowWebService;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {

    private final BookBorrowWebService bookBorrowWebService;

    public StudentController(BookBorrowWebService bookBorrowWebService) {
        this.bookBorrowWebService = bookBorrowWebService;
    }

    @GetMapping("/{id}/borrows")
    public Result<List<BookBorrow>> getStudentBorrows(@PathVariable @NotNull(message = "学生ID不能为空") Long id) {
        List<BookBorrow> borrows = bookBorrowWebService.getBorrowsByStudentId(id);
        return Result.success(borrows);
    }
}
