package com.yuxuanlei.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BookUpdateRequest {

    @NotNull(message = "图书ID不能为空")
    private Long id;

    @Size(max = 200, message = "图书名称长度不能超过200")
    private String bookName;

    @Size(max = 100, message = "作者名称长度不能超过100")
    private String author;

    @Size(max = 50, message = "ISBN长度不能超过50")
    private String isbn;

    @Positive(message = "总数量必须大于0")
    private Integer totalCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
