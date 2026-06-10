package com.yuxuanlei.web.service;

import com.yuxuanlei.entity.Book;
import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.entity.LibraryCard;
import com.yuxuanlei.entity.Student;
import com.yuxuanlei.enums.BorrowStatus;
import com.yuxuanlei.enums.CardStatus;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.mapper.BookBorrowMapper;
import com.yuxuanlei.mapper.BookMapper;
import com.yuxuanlei.mapper.LibraryCardMapper;
import com.yuxuanlei.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowWebService {

    private static final Logger logger = LoggerFactory.getLogger(BorrowWebService.class);

    @Autowired
    private BookBorrowMapper bookBorrowMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private LibraryCardMapper libraryCardMapper;

    @Transactional
    public BookBorrow borrowBook(Long studentId, Long bookId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new DataNotFoundException("学生不存在，ID：" + studentId);
        }

        LibraryCard card = libraryCardMapper.selectByStudentId(studentId);
        if (card == null) {
            throw new BusinessException("该学生未办理借书证");
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("借书证状态无效，当前状态：" + card.getStatus());
        }

        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + bookId);
        }
        if (book.getAvailableCount() == null || book.getAvailableCount() <= 0) {
            throw new BusinessException("图书库存不足，当前可借数量：" + book.getAvailableCount());
        }

        BookBorrow existingBorrow = bookBorrowMapper.selectActiveBorrow(studentId, bookId);
        if (existingBorrow != null) {
            throw new BusinessException("该学生已借阅此书且未归还，不能重复借阅");
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        bookMapper.updateById(book);

        BookBorrow borrow = new BookBorrow();
        borrow.setStudentId(studentId);
        borrow.setBookId(bookId);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.BORROWED);
        bookBorrowMapper.insert(borrow);

        logger.info("借书成功，borrowId={}, studentId={}, bookId={}", borrow.getId(), studentId, bookId);
        return borrow;
    }

    @Transactional
    public BookBorrow returnBook(Long borrowId) {
        BookBorrow borrow = bookBorrowMapper.selectById(borrowId);
        if (borrow == null) {
            throw new DataNotFoundException("借阅记录不存在，ID：" + borrowId);
        }
        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessException("该书已归还，请勿重复操作");
        }

        Book book = bookMapper.selectById(borrow.getBookId());
        if (book != null) {
            book.setAvailableCount(book.getAvailableCount() + 1);
            bookMapper.updateById(book);
        }

        borrow.setReturnDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.RETURNED);
        bookBorrowMapper.updateById(borrow);

        logger.info("还书成功，borrowId={}", borrowId);
        return borrow;
    }

    public List<BookBorrow> getStudentBorrows(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new DataNotFoundException("学生不存在，ID：" + studentId);
        }
        return bookBorrowMapper.selectBorrowsByStudentId(studentId);
    }
}
