package com.yuxuanlei.mapper;

import com.yuxuanlei.entity.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书 Mapper
 */
public interface BookMapper {
    
    /**
     * 插入图书
     */
    int insert(Book book);
    
    /**
     * 根据ID更新图书
     */
    int updateById(Book book);
    
    /**
     * 根据ID删除图书
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID查询图书
     */
    Book selectById(@Param("id") Long id);
    
    /**
     * 分页查询图书列表
     */
    List<Book> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询图书总数
     */
    long selectCount();
    
    /**
     * 条件检索图书
     */
    List<Book> searchBooks(@Param("bookName") String bookName,
                          @Param("author") String author,
                          @Param("isbn") String isbn,
                          @Param("offset") int offset,
                          @Param("limit") int limit);
    
    /**
     * 条件检索图书总数
     */
    long searchCount(@Param("bookName") String bookName,
                    @Param("author") String author,
                    @Param("isbn") String isbn);
    
    /**
     * 根据学生ID查询所借图书（Prompt：学生和所借图书 1:N 级联）
     */
    List<Book> selectBooksBorrowedByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据ISBN查询图书
     */
    Book selectByIsbn(@Param("isbn") String isbn);
    
    /**
     * 条件检索图书（返回总数）
     */
    long searchBooksCount(@Param("bookName") String bookName,
                         @Param("author") String author,
                         @Param("isbn") String isbn);
}
