package com.yuxuanlei.web.service;

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
import com.yuxuanlei.web.dto.BorrowRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 借阅 Web 服务层。借书时进行三重前置校验：
 * <ol>
 *   <li>学生借书证状态必须为 ACTIVE 且未过期</li>
 *   <li>图书库存（availableCount）必须大于 0</li>
 *   <li>同一学生不能重复借阅同一本未归还的图书</li>
 * </ol>
 */
@Service
public class BorrowWebService {

    private static final Logger logger = LoggerFactory.getLogger(BorrowWebService.class);

    private final BookBorrowMapper borrowMapper;
    private final BookMapper bookMapper;
    private final LibraryCardMapper cardMapper;

    public BorrowWebService(BookBorrowMapper borrowMapper,
                            BookMapper bookMapper,
                            LibraryCardMapper cardMapper) {
        this.borrowMapper = borrowMapper;
        this.bookMapper = bookMapper;
        this.cardMapper = cardMapper;
    }

    /**
     * 借书：执行三重校验后扣减库存并写入借阅记录。
     */
    @Transactional
    public BookBorrow borrow(BorrowRequest request) {
        Long studentId = request.getStudentId();
        Long bookId = request.getBookId();

        // 1) 借书证有效性校验
        LibraryCard card = cardMapper.selectByStudentId(studentId);
        if (card == null) {
            throw new BusinessException("学生尚未办理借书证，studentId=" + studentId);
        }
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("借书证状态无效，当前状态：" + card.getStatus());
        }
        if (card.getExpireDate() != null && card.getExpireDate().isBefore(LocalDate.now())) {
            throw new BusinessException("借书证已过期，过期日期：" + card.getExpireDate());
        }

        // 2) 图书库存校验
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new DataNotFoundException("图书不存在，ID：" + bookId);
        }
        if (book.getAvailableCount() == null || book.getAvailableCount() <= 0) {
            throw new BusinessException("图书库存不足，bookId=" + bookId);
        }

        // 3) 同一学生不能重复借阅同一本未归还的图书
        List<BookBorrow> studentBorrows = borrowMapper.selectBorrowsByStudentId(studentId);
        boolean alreadyBorrowed = studentBorrows.stream()
                .anyMatch(b -> bookId.equals(b.getBookId())
                        && b.getStatus() == BorrowStatus.BORROWED);
        if (alreadyBorrowed) {
            throw new BusinessException("您已借阅该图书且尚未归还，bookId=" + bookId);
        }

        // 扣减库存
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookMapper.updateById(book);

        // 写入借阅记录
        BookBorrow borrow = new BookBorrow();
        borrow.setStudentId(studentId);
        borrow.setBookId(bookId);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.BORROWED);
        borrowMapper.insert(borrow);
        logger.info("借书成功：studentId={}, bookId={}, borrowId={}",
                studentId, bookId, borrow.getId());
        return borrowMapper.selectBorrowWithDetails(borrow.getId());
    }

    /**
     * 还书：根据借阅记录 ID 归还，恢复库存。
     */
    @Transactional
    public BookBorrow returnBook(Long borrowId) {
        BookBorrow borrow = borrowMapper.selectById(borrowId);
        if (borrow == null) {
            throw new DataNotFoundException("借阅记录不存在，ID：" + borrowId);
        }
        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessException("该书已归还，borrowId=" + borrowId);
        }

        Book book = bookMapper.selectById(borrow.getBookId());
        if (book != null) {
            book.setAvailableCount(book.getAvailableCount() + 1);
            bookMapper.updateById(book);
        }

        borrow.setReturnDate(LocalDateTime.now());
        borrow.setStatus(BorrowStatus.RETURNED);
        borrowMapper.updateById(borrow);
        logger.info("还书成功：borrowId={}", borrowId);
        return borrowMapper.selectBorrowWithDetails(borrowId);
    }

    /**
     * 查询学生的借阅记录（含图书信息）。
     */
    public List<BookBorrow> getBorrowsByStudent(Long studentId) {
        return borrowMapper.selectBorrowsByStudentId(studentId);
    }

    /**
     * 查询单条借阅记录详情。
     */
    public BookBorrow getBorrowDetails(Long borrowId) {
        BookBorrow borrow = borrowMapper.selectBorrowWithDetails(borrowId);
        if (borrow == null) {
            throw new DataNotFoundException("借阅记录不存在，ID：" + borrowId);
        }
        return borrow;
    }
}
