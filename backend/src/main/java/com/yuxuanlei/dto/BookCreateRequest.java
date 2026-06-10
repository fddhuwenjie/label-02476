package com.yuxuanlei.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BookCreateRequest {

    @NotBlank(message = "图书名称不能为空")
    @Size(max = 200, message = "图书名称长度不能超过200")
    private String bookName;

    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者名称长度不能超过100")
    private String author;

    @NotBlank(message = "ISBN不能为空")
    @Size(max = 50, message = "ISBN长度不能超过50")
    private String isbn;

    @NotNull(message = "总数量不能为空")
    @Positive(message = "总数量必须大于0")
    private Integer totalCount;

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
