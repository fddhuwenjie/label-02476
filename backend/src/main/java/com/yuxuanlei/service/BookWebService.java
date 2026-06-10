package com.yuxuanlei.service;

import com.yuxuanlei.entity.Book;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.BookMapper;
import com.yuxuanlei.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookWebService {

    private static final Logger logger = LoggerFactory.getLogger(BookWebService.class);

    private final BookMapper bookMapper;

    public BookWebService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Transactional
    public Long addBook(Book book) {
        logger.info("新增图书：{}", book.getBookName());

        Book existing = bookMapper.selectByIsbn(book.getIsbn());
        if (existing != null) {
            throw new DuplicateDataException("ISBN " + book.getIsbn() + " 已存在");
        }

        if (book.getAvailableCount() == null) {
            book.setAvailableCount(book.getTotalCount());
        }

        int rows = bookMapper.insert(book);
        if (rows <= 0) {
            throw new BusinessException("新增图书失败");
        }

        logger.info("新增图书成功，ID：{}", book.getId());
        return book.getId();
    }

    @Transactional
    public boolean updateBook(Book book) {
        logger.info("更新图书信息：ID={}", book.getId());

        Book existing = bookMapper.selectById(book.getId());
        if (existing == null) {
            throw new DataNotFoundException("图书不存在，ID：" + book.getId());
        }

        if (book.getBookName() != null) {
            existing.setBookName(book.getBookName());
        }
        if (book.getAuthor() != null) {
            existing.setAuthor(book.getAuthor());
        }
        if (book.getIsbn() != null) {
            Book isbnBook = bookMapper.selectByIsbn(book.getIsbn());
            if (isbnBook != null && !isbnBook.getId().equals(book.getId())) {
                throw new DuplicateDataException("ISBN " + book.getIsbn() + " 已存在");
            }
            existing.setIsbn(book.getIsbn());
        }
        if (book.getTotalCount() != null) {
            existing.setTotalCount(book.getTotalCount());
        }

        int rows = bookMapper.updateById(existing);
        logger.info("更新图书成功，ID：{}", book.getId());
        return rows > 0;
    }

    @Transactional
    public boolean deleteBook(Long id) {
        logger.info("删除图书：ID={}", id);

        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }

        int rows = bookMapper.deleteById(id);
        logger.info("删除图书成功，ID：{}", id);
        return rows > 0;
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

    public PageResult<Book> searchBooks(int pageNum, int pageSize, String bookName, String author, String isbn) {
        int offset = (pageNum - 1) * pageSize;
        List<Book> records = bookMapper.searchBooks(bookName, author, isbn, offset, pageSize);
        long total = bookMapper.searchBooksCount(bookName, author, isbn);
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    @Transactional
    public boolean decreaseAvailableCount(Long bookId) {
        logger.info("减少图书可借数量：bookId={}", bookId);

        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + bookId);
        }
        if (book.getAvailableCount() <= 0) {
            throw new BusinessException("图书库存不足");
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        int rows = bookMapper.updateById(book);
        logger.info("减少图书可借数量成功，bookId={}，剩余：{}", bookId, book.getAvailableCount());
        return rows > 0;
    }

    @Transactional
    public boolean increaseAvailableCount(Long bookId) {
        logger.info("增加图书可借数量：bookId={}", bookId);

        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + bookId);
        }

        book.setAvailableCount(book.getAvailableCount() + 1);
        int rows = bookMapper.updateById(book);
        logger.info("增加图书可借数量成功，bookId={}，当前：{}", bookId, book.getAvailableCount());
        return rows > 0;
    }
}
