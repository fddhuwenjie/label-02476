package com.yuxuanlei.controller;

import com.yuxuanlei.common.Result;
import com.yuxuanlei.dto.BookCreateRequest;
import com.yuxuanlei.dto.BookUpdateRequest;
import com.yuxuanlei.entity.Book;
import com.yuxuanlei.service.BookWebService;
import com.yuxuanlei.util.PageResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private final BookWebService bookWebService;

    public BookController(BookWebService bookWebService) {
        this.bookWebService = bookWebService;
    }

    @GetMapping
    public Result<PageResult<Book>> listBooks(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") int pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") int pageSize,
            @RequestParam(required = false) String bookName,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn) {

        PageResult<Book> result;
        if (bookName != null || author != null || isbn != null) {
            result = bookWebService.searchBooks(pageNum, pageSize, bookName, author, isbn);
        } else {
            result = bookWebService.listBooks(pageNum, pageSize);
        }
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable @NotNull(message = "图书ID不能为空") Long id) {
        Book book = bookWebService.getBookById(id);
        return Result.success(book);
    }

    @PostMapping
    public Result<Long> createBook(@RequestBody @Valid BookCreateRequest request) {
        Book book = new Book();
        book.setBookName(request.getBookName());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCount(request.getTotalCount());
        Long id = bookWebService.addBook(book);
        return Result.success(id);
    }

    @PutMapping
    public Result<Void> updateBook(@RequestBody @Valid BookUpdateRequest request) {
        Book book = new Book();
        book.setId(request.getId());
        book.setBookName(request.getBookName());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCount(request.getTotalCount());
        bookWebService.updateBook(book);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable @NotNull(message = "图书ID不能为空") Long id) {
        bookWebService.deleteBook(id);
        return Result.success();
    }
}
