package com.yuxuanlei.service;

import com.yuxuanlei.entity.Book;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.BookMapper;
import com.yuxuanlei.util.MyBatisUtil;
import com.yuxuanlei.util.PageResult;
import com.yuxuanlei.util.ValidationUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 图书服务类
 */
public class BookService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    /**
     * 新增图书
     */
    public Long addBook(Book book) {
        // 输入校验
        ValidationUtil.validateBook(book);
        
        logger.info("新增图书：{}", book.getBookName());
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
            
            // 检查ISBN是否重复
            Book existing = mapper.selectByIsbn(book.getIsbn());
            if (existing != null) {
                throw new DuplicateDataException("ISBN " + book.getIsbn() + " 已存在");
            }
            
            // 设置默认可借数量
            if (book.getAvailableCount() == null) {
                book.setAvailableCount(book.getTotalCount());
            }
            
            mapper.insert(book);
            sqlSession.commit();
            logger.info("新增图书成功，ID：{}", book.getId());
            return book.getId();
        } catch (DuplicateDataException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("新增图书失败：{}", book.getBookName(), e);
            throw new BusinessException("新增图书失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 更新图书信息
     */
    public boolean updateBook(Book book) {
        // 输入校验
        ValidationUtil.validateId(book.getId(), "图书ID");
        ValidationUtil.validateBook(book);
        
        logger.info("更新图书信息：ID={}", book.getId());
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
            
            // 检查图书是否存在
            Book existing = mapper.selectById(book.getId());
            if (existing == null) {
                throw new DataNotFoundException("图书不存在，ID：" + book.getId());
            }
            
            int rows = mapper.updateById(book);
            sqlSession.commit();
            logger.info("更新图书成功，ID：{}", book.getId());
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("更新图书失败，ID：{}", book.getId(), e);
            throw new BusinessException("更新图书失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 删除图书
     */
    public boolean deleteBook(Long id) {
        // 输入校验
        ValidationUtil.validateId(id, "图书ID");
        
        logger.info("删除图书：ID={}", id);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
            
            // 检查图书是否存在
            Book existing = mapper.selectById(id);
            if (existing == null) {
                throw new DataNotFoundException("图书不存在，ID：" + id);
            }
            
            int rows = mapper.deleteById(id);
            sqlSession.commit();
            logger.info("删除图书成功，ID：{}", id);
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("删除图书失败，ID：{}", id, e);
            throw new BusinessException("删除图书失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 根据ID查询图书
     */
    public Book getBookById(Long id) {
        // 输入校验
        ValidationUtil.validateId(id, "图书ID");
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
            return mapper.selectById(id);
        } catch (Exception e) {
            logger.error("查询图书失败，ID：{}", id, e);
            throw new BusinessException("查询图书失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 分页查询图书列表
     */
    public PageResult<Book> listBooks(int pageNum, int pageSize) {
        // 输入校验
        ValidationUtil.validatePageParams(pageNum, pageSize);
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
            int offset = (pageNum - 1) * pageSize;
            List<Book> records = mapper.selectList(offset, pageSize);
            long total = mapper.selectCount();
            return new PageResult<>(records, total, pageNum, pageSize);
        } catch (Exception e) {
            logger.error("分页查询图书失败，pageNum：{}，pageSize：{}", pageNum, pageSize, e);
            throw new BusinessException("分页查询图书失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 条件检索图书
     */
    public PageResult<Book> searchBooks(int pageNum, int pageSize, String bookName, String author, String isbn) {
        // 输入校验
        ValidationUtil.validatePageParams(pageNum, pageSize);
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
            int offset = (pageNum - 1) * pageSize;
            List<Book> records = mapper.searchBooks(bookName, author, isbn, offset, pageSize);
            long total = mapper.searchBooksCount(bookName, author, isbn);
            return new PageResult<>(records, total, pageNum, pageSize);
        } catch (Exception e) {
            logger.error("条件检索图书失败", e);
            throw new BusinessException("条件检索图书失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 减少可借数量
     */
    public boolean decreaseAvailableCount(Long bookId) {
        // 输入校验
        ValidationUtil.validateId(bookId, "图书ID");
        
        logger.info("减少图书可借数量：bookId={}", bookId);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
            
            Book book = mapper.selectById(bookId);
            if (book == null) {
                throw new DataNotFoundException("图书不存在，ID：" + bookId);
            }
            if (book.getAvailableCount() <= 0) {
                throw new BusinessException("图书库存不足");
            }
            
            book.setAvailableCount(book.getAvailableCount() - 1);
            int rows = mapper.updateById(book);
            sqlSession.commit();
            logger.info("减少图书可借数量成功，bookId={}，剩余：{}", bookId, book.getAvailableCount());
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (BusinessException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("减少图书可借数量失败，bookId：{}", bookId, e);
            throw new BusinessException("减少图书可借数量失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 增加可借数量
     */
    public boolean increaseAvailableCount(Long bookId) {
        // 输入校验
        ValidationUtil.validateId(bookId, "图书ID");
        
        logger.info("增加图书可借数量：bookId={}", bookId);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            BookMapper mapper = sqlSession.getMapper(BookMapper.class);
            
            Book book = mapper.selectById(bookId);
            if (book == null) {
                throw new DataNotFoundException("图书不存在，ID：" + bookId);
            }
            
            book.setAvailableCount(book.getAvailableCount() + 1);
            int rows = mapper.updateById(book);
            sqlSession.commit();
            logger.info("增加图书可借数量成功，bookId={}，当前：{}", bookId, book.getAvailableCount());
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("增加图书可借数量失败，bookId：{}", bookId, e);
            throw new BusinessException("增加图书可借数量失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
}
