package com.yuxuanlei.web.service;

import com.yuxuanlei.dto.BookCreateDTO;
import com.yuxuanlei.dto.BookUpdateDTO;
import com.yuxuanlei.entity.Book;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.BookMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookWebService {

    private static final Logger logger = LoggerFactory.getLogger(BookWebService.class);

    @Autowired
    private BookMapper bookMapper;

    public List<Book> listBooks(int pageNum, int pageSize) {
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        return bookMapper.selectList(offset, pageSize);
    }

    public Book getBookById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }
        return book;
    }

    @Transactional
    public Book createBook(BookCreateDTO dto) {
        Book existing = bookMapper.selectByIsbn(dto.getIsbn());
        if (existing != null) {
            throw new DuplicateDataException("ISBN " + dto.getIsbn() + " 已存在");
        }

        Book book = new Book();
        book.setBookName(dto.getBookName());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setTotalCount(dto.getTotalCount());
        if (dto.getAvailableCount() != null) {
            if (dto.getAvailableCount() > dto.getTotalCount()) {
                throw new BusinessException("可借数量不能大于总数量");
            }
            book.setAvailableCount(dto.getAvailableCount());
        } else {
            book.setAvailableCount(dto.getTotalCount());
        }

        bookMapper.insert(book);
        logger.info("新增图书成功，ID：{}", book.getId());
        return book;
    }

    @Transactional
    public Book updateBook(Long id, BookUpdateDTO dto) {
        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }

        if (dto.getAvailableCount() > dto.getTotalCount()) {
            throw new BusinessException("可借数量不能大于总数量");
        }

        existing.setBookName(dto.getBookName());
        existing.setAuthor(dto.getAuthor());
        existing.setIsbn(dto.getIsbn());
        existing.setTotalCount(dto.getTotalCount());
        existing.setAvailableCount(dto.getAvailableCount());

        bookMapper.updateById(existing);
        logger.info("更新图书成功，ID：{}", id);
        return existing;
    }

    @Transactional
    public void deleteBook(Long id) {
        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            throw new DataNotFoundException("图书不存在，ID：" + id);
        }
        bookMapper.deleteById(id);
        logger.info("删除图书成功，ID：{}", id);
    }
}
