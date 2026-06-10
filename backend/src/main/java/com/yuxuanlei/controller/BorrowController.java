package com.yuxuanlei.controller;

import com.yuxuanlei.common.Result;
import com.yuxuanlei.dto.BorrowRequest;
import com.yuxuanlei.dto.ReturnRequest;
import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.service.BorrowApiService;
import com.yuxuanlei.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@Validated
public class BorrowController {

    @Autowired
    private BorrowApiService borrowApiService;

    @GetMapping
    public Result<PageResult<BookBorrow>> listBorrows(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(borrowApiService.listBorrows(pageNum, pageSize));
    }

    @PostMapping
    public Result<Long> borrowBook(@Validated @RequestBody BorrowRequest request) {
        return Result.success(borrowApiService.borrowBook(request));
    }

    @PostMapping("/return")
    public Result<Boolean> returnBook(@Validated @RequestBody ReturnRequest request) {
        return Result.success(borrowApiService.returnBook(request));
    }
}
