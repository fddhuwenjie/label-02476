package com.yuxuanlei.web.service;

import com.yuxuanlei.entity.Book;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.BookMapper;
import com.yuxuanlei.util.PageResult;
import com.yuxuanlei.web.dto.BookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 图书 Web 服务层。直接调用 {@link BookMapper}（XML Mapper），
 * 由 Spring 管理事务与 SqlSession。
 */
@Service
public class BookWebService {

    private static final Logger logger = LoggerFactory.getLogger(BookWebService.class);

    private final BookMapper bookMapper;

    public BookWebService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    /**
     * 新增图书
     */
    @Transactional
    public Book addBook(BookRequest request) {
        if (bookMapper.selectByIsbn(request.getIsbn()) != null) {
            throw new DuplicateDataException("ISBN " + request.getIsbn() + " 已存在");
        }
        Book book = new Book();
        book.setBookName(request.getBookName());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCount(request.getTotalCount());
        book.setAvailableCount(request.getAvailableCount() != null
                ? request.getAvailableCount()
                : request.getTotalCount());
        bookMapper.insert(book);
        logger.info("新增图书成功，ID={}", book.getId());
        return bookMapper.selectById(book.getId());
    }

    /**
     * 更新图书
     */
    @Transactional
    public Book updateBook(Long id, BookRequest request) {
        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }
        existing.setBookName(request.getBookName());
        existing.setAuthor(request.getAuthor());
        existing.setIsbn(request.getIsbn());
        if (request.getTotalCount() != null) {
            existing.setTotalCount(request.getTotalCount());
        }
        if (request.getAvailableCount() != null) {
            existing.setAvailableCount(request.getAvailableCount());
        }
        if (existing.getAvailableCount() != null && existing.getTotalCount() != null
                && existing.getAvailableCount() > existing.getTotalCount()) {
            throw new BusinessException("可借数量不能大于总数量");
        }
        bookMapper.updateById(existing);
        return bookMapper.selectById(id);
    }

    /**
     * 删除图书
     */
    @Transactional
    public void deleteBook(Long id) {
        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }
        bookMapper.deleteById(id);
    }

    /**
     * 根据 ID 查询图书
     */
    public Book getBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }
        return book;
    }

    /**
     * 分页查询图书列表（支持条件检索）
     */
    public PageResult<Book> listBooks(int pageNum, int pageSize,
                                      String bookName, String author, String isbn) {
        int offset = (pageNum - 1) * pageSize;
        boolean hasCondition = (bookName != null && !bookName.isBlank())
                || (author != null && !author.isBlank())
                || (isbn != null && !isbn.isBlank());
        List<Book> records;
        long total;
        if (hasCondition) {
            records = bookMapper.searchBooks(bookName, author, isbn, offset, pageSize);
            total = bookMapper.searchBooksCount(bookName, author, isbn);
        } else {
            records = bookMapper.selectList(offset, pageSize);
            total = bookMapper.selectCount();
        }
        return new PageResult<>(records, total, pageNum, pageSize);
    }
}
