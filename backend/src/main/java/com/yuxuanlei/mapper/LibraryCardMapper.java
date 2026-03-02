package com.yuxuanlei.mapper;

import com.yuxuanlei.entity.LibraryCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 借书证 Mapper
 */
public interface LibraryCardMapper {
    
    /**
     * 插入借书证
     */
    int insert(LibraryCard card);
    
    /**
     * 根据ID更新借书证
     */
    int updateById(LibraryCard card);
    
    /**
     * 根据ID查询借书证
     */
    LibraryCard selectById(@Param("id") Long id);
    
    /**
     * 根据学生ID查询借书证
     */
    LibraryCard selectByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据ID查询借书证（含关联学生信息，双向 1:1）
     */
    LibraryCard selectByIdWithStudent(@Param("id") Long id);
    
    /**
     * 根据学生ID查询借书证（含关联学生信息）
     */
    LibraryCard selectByStudentIdWithStudent(@Param("studentId") Long studentId);
    
    /**
     * 分页查询借书证列表
     */
    List<LibraryCard> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询借书证总数
     */
    long selectCount();
}
