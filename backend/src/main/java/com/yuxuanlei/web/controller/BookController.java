package com.yuxuanlei.web.controller;

import com.yuxuanlei.entity.Book;
import com.yuxuanlei.util.PageResult;
import com.yuxuanlei.web.common.Result;
import com.yuxuanlei.web.dto.BookRequest;
import com.yuxuanlei.web.service.BookWebService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 图书 CRUD REST 接口
 */
@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private final BookWebService bookService;

    public BookController(BookWebService bookService) {
        this.bookService = bookService;
    }

    /**
     * 分页/条件查询图书列表
     */
    @GetMapping
    public Result<PageResult<Book>> list(
            @RequestParam(value = "pageNum", defaultValue = "1")
            @Min(value = 1, message = "页码必须大于0") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10")
            @Min(value = 1, message = "每页大小必须在1-100之间")
            @Max(value = 100, message = "每页大小必须在1-100之间") int pageSize,
            @RequestParam(value = "bookName", required = false) String bookName,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "isbn", required = false) String isbn) {
        return Result.success(bookService.listBooks(pageNum, pageSize, bookName, author, isbn));
    }

    /**
     * 查询单本图书
     */
    @GetMapping("/{id}")
    public Result<Book> get(@PathVariable("id") @Positive(message = "图书ID必须是正整数") Long id) {
        return Result.success(bookService.getBook(id));
    }

    /**
     * 新增图书
     */
    @PostMapping
    public Result<Book> create(@Valid @RequestBody BookRequest request) {
        return Result.success(bookService.addBook(request));
    }

    /**
     * 更新图书
     */
    @PutMapping("/{id}")
    public Result<Book> update(@PathVariable("id") @Positive(message = "图书ID必须是正整数") Long id,
                               @Valid @RequestBody BookRequest request) {
        return Result.success(bookService.updateBook(id, request));
    }

    /**
     * 删除图书（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @Positive(message = "图书ID必须是正整数") Long id) {
        bookService.deleteBook(id);
        return Result.success();
    }
}
