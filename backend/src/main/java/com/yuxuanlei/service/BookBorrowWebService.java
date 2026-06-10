package com.yuxuanlei.service;

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
import com.yuxuanlei.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookBorrowWebService {

    private static final Logger logger = LoggerFactory.getLogger(BookBorrowWebService.class);

    private final BookBorrowMapper bookBorrowMapper;
    private final BookMapper bookMapper;
    private final StudentMapper studentMapper;
    private final LibraryCardMapper libraryCardMapper;

    public BookBorrowWebService(BookBorrowMapper bookBorrowMapper, BookMapper bookMapper,
                                StudentMapper studentMapper, LibraryCardMapper libraryCardMapper) {
        this.bookBorrowMapper = bookBorrowMapper;
        this.bookMapper = bookMapper;
        this.studentMapper = studentMapper;
        this.libraryCardMapper = libraryCardMapper;
    }

    @Transactional
    public Long borrowBook(Long studentId, Long bookId) {
        logger.info("借书：studentId={}, bookId={}", studentId, bookId);

        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new DataNotFoundException("学生不存在，ID：" + studentId);
        }

        LibraryCard card = libraryCardMapper.selectByStudentId(studentId);
        if (card == null) {
            throw new BusinessException("学生没有借书证，studentId：" + studentId);
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("借书证状态无效，当前状态：" + card.getStatus());
        }

        var book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + bookId);
        }
        if (book.getAvailableCount() <= 0) {
            throw new BusinessException("图书库存不足");
        }

        List<BookBorrow> studentBorrows = bookBorrowMapper.selectBorrowsByStudentId(studentId);
        boolean alreadyBorrowed = studentBorrows.stream()
                .anyMatch(b -> b.getBookId().equals(bookId) && b.getStatus() == BorrowStatus.BORROWED);
        if (alreadyBorrowed) {
            throw new BusinessException("该学生已借阅此书且未归还");
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        bookMapper.updateById(book);

        BookBorrow borrow = new BookBorrow();
        borrow.setStudentId(studentId);
        borrow.setBookId(bookId);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.BORROWED);

        bookBorrowMapper.insert(borrow);
        logger.info("借书成功，ID：{}", borrow.getId());
        return borrow.getId();
    }

    @Transactional
    public boolean returnBook(Long borrowId) {
        logger.info("还书：borrowId={}", borrowId);

        BookBorrow borrow = bookBorrowMapper.selectById(borrowId);
        if (borrow == null) {
            throw new DataNotFoundException("借阅记录不存在，ID：" + borrowId);
        }
        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessException("该书已归还");
        }

        var book = bookMapper.selectById(borrow.getBookId());
        if (book != null) {
            book.setAvailableCount(book.getAvailableCount() + 1);
            bookMapper.updateById(book);
        }

        borrow.setReturnDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.RETURNED);
        int rows = bookBorrowMapper.updateById(borrow);
        logger.info("还书成功，borrowId：{}", borrowId);
        return rows > 0;
    }

    public List<BookBorrow> getBorrowsByStudentId(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
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

    public BookBorrow getBorrowById(Long id) {
        BookBorrow borrow = bookBorrowMapper.selectById(id);
        if (borrow == null) {
            throw new DataNotFoundException("借阅记录不存在，ID：" + id);
        }
        return borrow;
    }
}
