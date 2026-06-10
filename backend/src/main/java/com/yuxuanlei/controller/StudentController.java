package com.yuxuanlei.controller;

import com.yuxuanlei.common.Result;
import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.service.BorrowApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {

    @Autowired
    private BorrowApiService borrowApiService;

    @GetMapping("/{id}/borrows")
    public Result<List<BookBorrow>> getStudentBorrows(@PathVariable Long id) {
        return Result.success(borrowApiService.getBorrowsByStudentId(id));
    }
}
