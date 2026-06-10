package com.yuxuanlei.service;

import com.yuxuanlei.dto.BorrowRequest;
import com.yuxuanlei.dto.ReturnRequest;
import com.yuxuanlei.entity.Book;
import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.entity.LibraryCard;
import com.yuxuanlei.enums.BorrowStatus;
import com.yuxuanlei.enums.CardStatus;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.mapper.BookBorrowMapper;
import com.yuxuanlei.mapper.BookMapper;
import com.yuxuanlei.mapper.LibraryCardMapper;
import com.yuxuanlei.mapper.StudentMapper;
import com.yuxuanlei.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowApiService {

    @Autowired
    private BookBorrowMapper bookBorrowMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private LibraryCardMapper libraryCardMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Transactional
    public Long borrowBook(BorrowRequest request) {
        Long studentId = request.getStudentId();
        Long bookId = request.getBookId();

        if (studentMapper.selectById(studentId) == null) {
            throw new DataNotFoundException("学生不存在，ID：" + studentId);
        }

        LibraryCard card = libraryCardMapper.selectByStudentId(studentId);
        if (card == null) {
            throw new BusinessException("该学生未办理借书证，无法借书");
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("借书证状态无效（当前状态：" + card.getStatus() + "），无法借书");
        }

        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + bookId);
        }
        if (book.getAvailableCount() <= 0) {
            throw new BusinessException("图书库存不足，无法借阅");
        }

        List<BookBorrow> existingBorrows = bookBorrowMapper.selectBorrowsByStudentId(studentId);
        boolean alreadyBorrowed = existingBorrows.stream()
                .anyMatch(b -> b.getBookId().equals(bookId) && b.getStatus() == BorrowStatus.BORROWED);
        if (alreadyBorrowed) {
            throw new BusinessException("该学生已借阅此书且尚未归还，不可重复借阅");
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        bookMapper.updateById(book);

        BookBorrow borrow = new BookBorrow();
        borrow.setStudentId(studentId);
        borrow.setBookId(bookId);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.BORROWED);

        bookBorrowMapper.insert(borrow);
        return borrow.getId();
    }

    @Transactional
    public boolean returnBook(ReturnRequest request) {
        Long borrowId = request.getBorrowId();

        BookBorrow borrow = bookBorrowMapper.selectById(borrowId);
        if (borrow == null) {
            throw new DataNotFoundException("借阅记录不存在，ID：" + borrowId);
        }
        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessException("该书已归还");
        }

        Book book = bookMapper.selectById(borrow.getBookId());
        if (book != null) {
            book.setAvailableCount(book.getAvailableCount() + 1);
            bookMapper.updateById(book);
        }

        borrow.setReturnDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.RETURNED);
        return bookBorrowMapper.updateById(borrow) > 0;
    }

    public List<BookBorrow> getBorrowsByStudentId(Long studentId) {
        if (studentMapper.selectById(studentId) == null) {
            throw new DataNotFoundException("学生不存在，ID：" + studentId);
        }
        return bookBorrowMapper.selectBorrowsByStudentId(studentId);
    }

    public PageResult<BookBorrow> listBorrows(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<BookBorrow> records = bookBorrowMapper.selectList(offset, pageSize);
        long total = bookBorrowMapper.selectCount();
        return new PageResult<>(records, total, pageNum, pageSize);
    }
}
