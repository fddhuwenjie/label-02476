package com.yuxuanlei.mapper;

import com.yuxuanlei.entity.BookBorrow;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 借阅记录 Mapper
 */
public interface BookBorrowMapper {
    
    /**
     * 插入借阅记录
     */
    int insert(BookBorrow borrow);
    
    /**
     * 根据ID更新借阅记录
     */
    int updateById(BookBorrow borrow);
    
    /**
     * 根据ID查询借阅记录
     */
    BookBorrow selectById(@Param("id") Long id);
    
    /**
     * 查询学生借阅记录（含图书信息）
     */
    List<BookBorrow> selectBorrowsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 查询借阅记录详情（含学生和图书信息）
     */
    BookBorrow selectBorrowWithDetails(@Param("id") Long id);

    /**
     * 查询学生对某本书的未归还借阅记录
     */
    BookBorrow selectActiveBorrow(@Param("studentId") Long studentId, @Param("bookId") Long bookId);
    
    /**
     * 分页查询借阅记录列表
     */
    List<BookBorrow> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询借阅记录总数
     */
    long selectCount();
}
