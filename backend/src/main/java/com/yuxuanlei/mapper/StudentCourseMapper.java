package com.yuxuanlei.mapper;

import com.yuxuanlei.entity.StudentCourse;
import org.apache.ibatis.annotations.Param;

/**
 * 学生选课 Mapper
 */
public interface StudentCourseMapper {
    
    /**
     * 插入选课记录
     */
    int insert(StudentCourse studentCourse);
    
    /**
     * 根据ID更新选课记录
     */
    int updateById(StudentCourse studentCourse);
    
    /**
     * 根据学生ID和课程ID删除选课记录
     */
    int deleteByStudentIdAndCourseId(@Param("studentId") Long studentId, 
                                     @Param("courseId") Long courseId);
    
    /**
     * 根据学生ID和课程ID查询选课记录
     */
    StudentCourse selectByStudentIdAndCourseId(@Param("studentId") Long studentId,
                                               @Param("courseId") Long courseId);
    
    /**
     * 根据学生ID和课程ID查询选课记录（别名方法）
     */
    default StudentCourse selectByStudentAndCourse(Long studentId, Long courseId) {
        return selectByStudentIdAndCourseId(studentId, courseId);
    }
    
    /**
     * 根据学生ID和课程ID删除选课记录（别名方法）
     */
    default int deleteByStudentAndCourse(Long studentId, Long courseId) {
        return deleteByStudentIdAndCourseId(studentId, courseId);
    }
}
