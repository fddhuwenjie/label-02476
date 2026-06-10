package com.yuxuanlei.controller;

import com.yuxuanlei.common.Result;
import com.yuxuanlei.dto.BookCreateDTO;
import com.yuxuanlei.dto.BookUpdateDTO;
import com.yuxuanlei.entity.Book;
import com.yuxuanlei.web.service.BookWebService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    @Autowired
    private BookWebService bookWebService;

    @GetMapping
    public Result<List<Book>> listBooks(
            @RequestParam(defaultValue = "1") @Positive(message = "页码必须大于0") int pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小必须大于0") int pageSize) {
        List<Book> books = bookWebService.listBooks(pageNum, pageSize);
        return Result.success(books);
    }

    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable @NotNull(message = "图书ID不能为空") @Positive(message = "图书ID必须为正整数") Long id) {
        Book book = bookWebService.getBookById(id);
        return Result.success(book);
    }

    @PostMapping
    public Result<Book> createBook(@Valid @RequestBody BookCreateDTO dto) {
        Book book = bookWebService.createBook(dto);
        return Result.success(book);
    }

    @PutMapping("/{id}")
    public Result<Book> updateBook(
            @PathVariable @NotNull(message = "图书ID不能为空") @Positive(message = "图书ID必须为正整数") Long id,
            @Valid @RequestBody BookUpdateDTO dto) {
        Book book = bookWebService.updateBook(id, dto);
        return Result.success(book);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable @NotNull(message = "图书ID不能为空") @Positive(message = "图书ID必须为正整数") Long id) {
        bookWebService.deleteBook(id);
        return Result.success();
    }
}
