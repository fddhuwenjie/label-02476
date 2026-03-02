package com.yuxuanlei.mapper;

import com.yuxuanlei.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生 Mapper
 */
public interface StudentMapper {
    
    /**
     * 插入学生
     */
    int insert(Student student);
    
    /**
     * 根据ID更新学生
     */
    int updateById(Student student);
    
    /**
     * 根据ID删除学生（逻辑删除）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID查询学生
     */
    Student selectById(@Param("id") Long id);
    
    /**
     * 查询学生详情（含级联信息）
     */
    Student selectStudentWithDetails(@Param("id") Long id);
    
    /**
     * 分页查询学生列表
     */
    List<Student> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询学生总数
     */
    long selectCount();
    
    /**
     * 条件检索学生
     */
    List<Student> searchStudents(@Param("name") String name,
                                  @Param("studentNo") String studentNo,
                                  @Param("major") String major,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);
    
    /**
     * 条件检索学生总数
     */
    long searchCount(@Param("name") String name,
                     @Param("studentNo") String studentNo,
                     @Param("major") String major);
    
    /**
     * 根据学号查询学生
     */
    Student selectByStudentNo(@Param("studentNo") String studentNo);
    
    /**
     * 根据课程ID查询选修该课程的学生（M:N 级联用）
     */
    List<Student> selectByCourseId(@Param("courseId") Long courseId);
}
