package com.yuxuanlei.service;

import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.enums.BorrowStatus;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.mapper.BookBorrowMapper;
import com.yuxuanlei.util.MyBatisUtil;
import com.yuxuanlei.util.PageResult;
import com.yuxuanlei.util.ValidationUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 借阅管理服务类
 */
public class BookBorrowService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookBorrowService.class);
    
    private final BookService bookService;
    
    public BookBorrowService(BookService bookService) {
        this.bookService = bookService;
    }
    
    /**
     * 借书（1:N 级联）
     * <p>流程：校验 -> 减少图书可借数量 -> 插入借阅记录。异常时回滚保证库存一致。
     *
     * @param borrow 借阅记录（studentId、bookId 必填，status 默认 BORROWED）
     * @return 借阅记录ID
     */
    public Long borrowBook(BookBorrow borrow) {
        // 输入校验
        ValidationUtil.validateBookBorrow(borrow);
        
        logger.info("借书：studentId={}, bookId={}", borrow.getStudentId(), borrow.getBookId());
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookBorrowMapper mapper = sqlSession.getMapper(BookBorrowMapper.class);
            
            // 减少图书可借数量
            bookService.decreaseAvailableCount(borrow.getBookId());
            
            // 设置默认值
            if (borrow.getBorrowDate() == null) {
                borrow.setBorrowDate(LocalDateTime.now());
            }
            if (borrow.getStatus() == null) {
                borrow.setStatus(BorrowStatus.BORROWED);
            }
            
            mapper.insert(borrow);
            sqlSession.commit();
            logger.info("借书成功，ID：{}", borrow.getId());
            return borrow.getId();
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("借书失败：studentId={}, bookId={}", borrow.getStudentId(), borrow.getBookId(), e);
            throw new BusinessException("借书失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 还书
     * <p>流程：校验状态非已归还 -> 增加图书可借数量 -> 更新借阅记录状态为 RETURNED
     *
     * @param borrowId 借阅记录ID
     * @return 是否成功
     * @throws BusinessException 该书已归还
     */
    public boolean returnBook(Long borrowId) {
        // 输入校验
        ValidationUtil.validateId(borrowId, "借阅记录ID");
        
        logger.info("还书：borrowId={}", borrowId);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookBorrowMapper mapper = sqlSession.getMapper(BookBorrowMapper.class);
            
            BookBorrow borrow = mapper.selectById(borrowId);
            if (borrow == null) {
                throw new DataNotFoundException("借阅记录不存在，ID：" + borrowId);
            }
            if (borrow.getStatus() == BorrowStatus.RETURNED) {
                throw new BusinessException("该书已归还");
            }
            
            // 增加图书可借数量
            bookService.increaseAvailableCount(borrow.getBookId());
            
            // 更新借阅记录
            borrow.setReturnDate(LocalDateTime.now());
            borrow.setStatus(BorrowStatus.RETURNED);
            int rows = mapper.updateById(borrow);
            sqlSession.commit();
            logger.info("还书成功，borrowId：{}", borrowId);
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (BusinessException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("还书失败，borrowId：{}", borrowId, e);
            throw new BusinessException("还书失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 查询学生借阅记录
     */
    public List<BookBorrow> getBorrowsByStudentId(Long studentId) {
        // 输入校验
        ValidationUtil.validateId(studentId, "学生ID");
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookBorrowMapper mapper = sqlSession.getMapper(BookBorrowMapper.class);
            return mapper.selectBorrowsByStudentId(studentId);
        } catch (Exception e) {
            logger.error("查询学生借阅记录失败，studentId：{}", studentId, e);
            throw new BusinessException("查询学生借阅记录失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 分页查询借阅记录
     */
    public PageResult<BookBorrow> listBorrows(int pageNum, int pageSize) {
        // 输入校验
        ValidationUtil.validatePageParams(pageNum, pageSize);
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookBorrowMapper mapper = sqlSession.getMapper(BookBorrowMapper.class);
            int offset = (pageNum - 1) * pageSize;
            List<BookBorrow> records = mapper.selectList(offset, pageSize);
            long total = mapper.selectCount();
            return new PageResult<>(records, total, pageNum, pageSize);
        } catch (Exception e) {
            logger.error("分页查询借阅记录失败，pageNum：{}，pageSize：{}", pageNum, pageSize, e);
            throw new BusinessException("分页查询借阅记录失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 查询借阅记录详情
     */
    public BookBorrow getBorrowWithDetails(Long id) {
        // 输入校验
        ValidationUtil.validateId(id, "借阅记录ID");
        
        logger.info("查询借阅记录详情：ID={}", id);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookBorrowMapper mapper = sqlSession.getMapper(BookBorrowMapper.class);
            BookBorrow borrow = mapper.selectBorrowWithDetails(id);
            if (borrow == null) {
                throw new DataNotFoundException("借阅记录不存在，ID：" + id);
            }
            return borrow;
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询借阅记录详情失败，ID：{}", id, e);
            throw new BusinessException("查询借阅记录详情失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 根据ID查询借阅记录
     */
    public BookBorrow getBorrowById(Long id) {
        // 输入校验
        ValidationUtil.validateId(id, "借阅记录ID");
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookBorrowMapper mapper = sqlSession.getMapper(BookBorrowMapper.class);
            return mapper.selectById(id);
        } catch (Exception e) {
            logger.error("查询借阅记录失败，ID：{}", id, e);
            throw new BusinessException("查询借阅记录失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
}
