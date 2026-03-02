package com.yuxuanlei.mapper;

import com.yuxuanlei.entity.Course;
import com.yuxuanlei.entity.StudentCourse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程 Mapper
 */
public interface CourseMapper {
    
    /**
     * 插入课程
     */
    int insert(Course course);
    
    /**
     * 根据ID更新课程
     */
    int updateById(Course course);
    
    /**
     * 根据ID删除课程
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID查询课程
     */
    Course selectById(@Param("id") Long id);
    
    /**
     * 分页查询课程列表
     */
    List<Course> selectList(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查询课程总数
     */
    long selectCount();
    
    /**
     * 查询学生选课记录（含课程及成绩，score 属中间表 StudentCourse）
     */
    List<StudentCourse> selectCourseEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 查询课程及其选修学生（M:N 级联，含 students 的 ResultMap）
     */
    Course selectCourseWithStudents(@Param("courseId") Long courseId);
    
    /**
     * 根据课程代码查询课程
     */
    Course selectByCourseCode(@Param("courseCode") String courseCode);
}
