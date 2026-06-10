package com.yuxuanlei.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookUpdateDTO {

    @NotBlank(message = "图书名称不能为空")
    @Size(max = 200, message = "图书名称长度不能超过200个字符")
    private String bookName;

    @Size(max = 100, message = "作者名称长度不能超过100个字符")
    private String author;

    @Size(max = 20, message = "ISBN长度不能超过20个字符")
    private String isbn;

    @NotNull(message = "图书总数量不能为空")
    @Min(value = 0, message = "图书总数量不能为负数")
    private Integer totalCount;

    @NotNull(message = "图书可借数量不能为空")
    @Min(value = 0, message = "图书可借数量不能为负数")
    private Integer availableCount;

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getAvailableCount() { return availableCount; }
    public void setAvailableCount(Integer availableCount) { this.availableCount = availableCount; }
}
