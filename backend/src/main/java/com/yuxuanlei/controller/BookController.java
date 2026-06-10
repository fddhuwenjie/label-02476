package com.yuxuanlei.controller;

import com.yuxuanlei.common.Result;
import com.yuxuanlei.dto.BookCreateRequest;
import com.yuxuanlei.dto.BookUpdateRequest;
import com.yuxuanlei.entity.Book;
import com.yuxuanlei.service.BookApiService;
import com.yuxuanlei.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    @Autowired
    private BookApiService bookApiService;

    @GetMapping
    public Result<PageResult<Book>> listBooks(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(bookApiService.listBooks(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Book> getBook(@PathVariable Long id) {
        return Result.success(bookApiService.getBookById(id));
    }

    @PostMapping
    public Result<Long> createBook(@Validated @RequestBody BookCreateRequest request) {
        return Result.success(bookApiService.createBook(request));
    }

    @PutMapping
    public Result<Boolean> updateBook(@Validated @RequestBody BookUpdateRequest request) {
        return Result.success(bookApiService.updateBook(request));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteBook(@PathVariable Long id) {
        return Result.success(bookApiService.deleteBook(id));
    }
}
