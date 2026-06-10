package com.yuxuanlei.controller;

import com.yuxuanlei.common.Result;
import com.yuxuanlei.dto.BorrowBookRequest;
import com.yuxuanlei.dto.ReturnBookRequest;
import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.service.BookBorrowWebService;
import com.yuxuanlei.util.PageResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@Validated
public class BorrowController {

    private final BookBorrowWebService bookBorrowWebService;

    public BorrowController(BookBorrowWebService bookBorrowWebService) {
        this.bookBorrowWebService = bookBorrowWebService;
    }

    @GetMapping
    public Result<PageResult<BookBorrow>> listBorrows(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") int pageSize) {
        PageResult<BookBorrow> result = bookBorrowWebService.listBorrows(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BookBorrow> getBorrowById(@PathVariable @NotNull(message = "借阅记录ID不能为空") Long id) {
        BookBorrow borrow = bookBorrowWebService.getBorrowById(id);
        return Result.success(borrow);
    }

    @PostMapping
    public Result<Long> borrowBook(@RequestBody @Valid BorrowBookRequest request) {
        Long borrowId = bookBorrowWebService.borrowBook(request.getStudentId(), request.getBookId());
        return Result.success(borrowId);
    }

    @PostMapping("/return")
    public Result<Void> returnBook(@RequestBody @Valid ReturnBookRequest request) {
        bookBorrowWebService.returnBook(request.getBorrowId());
        return Result.success();
    }
}
