package com.yuxuanlei.service;

import com.yuxuanlei.dto.BookCreateRequest;
import com.yuxuanlei.dto.BookUpdateRequest;
import com.yuxuanlei.entity.Book;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.BookMapper;
import com.yuxuanlei.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookApiService {

    @Autowired
    private BookMapper bookMapper;

    @Transactional
    public Long createBook(BookCreateRequest request) {
        if (request.getIsbn() != null && !request.getIsbn().isBlank()) {
            Book existing = bookMapper.selectByIsbn(request.getIsbn());
            if (existing != null) {
                throw new DuplicateDataException("ISBN " + request.getIsbn() + " 已存在");
            }
        }

        Book book = new Book();
        book.setBookName(request.getBookName());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCount(request.getTotalCount());
        book.setAvailableCount(
                request.getAvailableCount() != null ? request.getAvailableCount() : request.getTotalCount()
        );

        bookMapper.insert(book);
        return book.getId();
    }

    @Transactional
    public boolean updateBook(BookUpdateRequest request) {
        Book existing = bookMapper.selectById(request.getId());
        if (existing == null) {
            throw new DataNotFoundException("图书不存在，ID：" + request.getId());
        }

        Book book = new Book();
        book.setId(request.getId());
        book.setBookName(request.getBookName() != null ? request.getBookName() : existing.getBookName());
        book.setAuthor(request.getAuthor() != null ? request.getAuthor() : existing.getAuthor());
        book.setIsbn(request.getIsbn() != null ? request.getIsbn() : existing.getIsbn());
        book.setTotalCount(request.getTotalCount() != null ? request.getTotalCount() : existing.getTotalCount());
        book.setAvailableCount(request.getAvailableCount() != null ? request.getAvailableCount() : existing.getAvailableCount());

        return bookMapper.updateById(book) > 0;
    }

    @Transactional
    public boolean deleteBook(Long id) {
        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }
        return bookMapper.deleteById(id) > 0;
    }

    public Book getBookById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }
        return book;
    }

    public PageResult<Book> listBooks(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Book> records = bookMapper.selectList(offset, pageSize);
        long total = bookMapper.selectCount();
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    @Transactional
    public boolean decreaseAvailableCount(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + bookId);
        }
        if (book.getAvailableCount() <= 0) {
            throw new BusinessException("图书库存不足");
        }
        book.setAvailableCount(book.getAvailableCount() - 1);
        return bookMapper.updateById(book) > 0;
    }

    @Transactional
    public boolean increaseAvailableCount(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + bookId);
        }
        book.setAvailableCount(book.getAvailableCount() + 1);
        return bookMapper.updateById(book) > 0;
    }
}
