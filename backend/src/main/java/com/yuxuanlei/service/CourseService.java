package com.yuxuanlei.service;

import com.yuxuanlei.entity.Course;
import com.yuxuanlei.entity.StudentCourse;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.CourseMapper;
import com.yuxuanlei.util.MyBatisUtil;
import com.yuxuanlei.util.PageResult;
import com.yuxuanlei.util.ValidationUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 课程服务类
 */
public class CourseService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    
    /**
     * 新增课程
     */
    public Long addCourse(Course course) {
        // 输入校验
        ValidationUtil.validateCourse(course);
        
        logger.info("新增课程：{}", course.getCourseName());
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            
            // 检查课程代码是否重复
            Course existing = mapper.selectByCourseCode(course.getCourseCode());
            if (existing != null) {
                throw new DuplicateDataException("课程代码 " + course.getCourseCode() + " 已存在");
            }
            
            mapper.insert(course);
            sqlSession.commit();
            logger.info("新增课程成功，ID：{}", course.getId());
            return course.getId();
        } catch (DuplicateDataException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("新增课程失败：{}", course.getCourseName(), e);
            throw new BusinessException("新增课程失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 更新课程信息
     */
    public boolean updateCourse(Course course) {
        // 输入校验
        ValidationUtil.validateId(course.getId(), "课程ID");
        ValidationUtil.validateCourse(course);
        
        logger.info("更新课程信息：ID={}", course.getId());
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            
            // 检查课程是否存在
            Course existing = mapper.selectById(course.getId());
            if (existing == null) {
                throw new DataNotFoundException("课程不存在，ID：" + course.getId());
            }
            
            int rows = mapper.updateById(course);
            sqlSession.commit();
            logger.info("更新课程成功，ID：{}", course.getId());
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("更新课程失败，ID：{}", course.getId(), e);
            throw new BusinessException("更新课程失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 删除课程
     */
    public boolean deleteCourse(Long id) {
        // 输入校验
        ValidationUtil.validateId(id, "课程ID");
        
        logger.info("删除课程：ID={}", id);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            
            // 检查课程是否存在
            Course existing = mapper.selectById(id);
            if (existing == null) {
                throw new DataNotFoundException("课程不存在，ID：" + id);
            }
            
            int rows = mapper.deleteById(id);
            sqlSession.commit();
            logger.info("删除课程成功，ID：{}", id);
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("删除课程失败，ID：{}", id, e);
            throw new BusinessException("删除课程失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 根据ID查询课程
     */
    public Course getCourseById(Long id) {
        // 输入校验
        ValidationUtil.validateId(id, "课程ID");
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            return mapper.selectById(id);
        } catch (Exception e) {
            logger.error("查询课程失败，ID：{}", id, e);
            throw new BusinessException("查询课程失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 分页查询课程列表
     */
    public PageResult<Course> listCourses(int pageNum, int pageSize) {
        // 输入校验
        ValidationUtil.validatePageParams(pageNum, pageSize);
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            int offset = (pageNum - 1) * pageSize;
            List<Course> records = mapper.selectList(offset, pageSize);
            long total = mapper.selectCount();
            return new PageResult<>(records, total, pageNum, pageSize);
        } catch (Exception e) {
            logger.error("分页查询课程失败，pageNum：{}，pageSize：{}", pageNum, pageSize, e);
            throw new BusinessException("分页查询课程失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 查询学生选课记录（含课程及成绩，score 属中间表 StudentCourse）
     */
    public List<StudentCourse> getCourseEnrollmentsByStudentId(Long studentId) {
        // 输入校验
        ValidationUtil.validateId(studentId, "学生ID");
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            return mapper.selectCourseEnrollmentsByStudentId(studentId);
        } catch (Exception e) {
            logger.error("查询学生选修课程失败，studentId：{}", studentId, e);
            throw new BusinessException("查询学生选修课程失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 查询课程及其选修学生（M:N 级联）
     */
    public Course getCourseWithStudents(Long courseId) {
        // 输入校验
        ValidationUtil.validateId(courseId, "课程ID");
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            return mapper.selectCourseWithStudents(courseId);
        } catch (Exception e) {
            logger.error("查询课程选修学生失败，courseId：{}", courseId, e);
            throw new BusinessException("查询课程选修学生失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 根据课程代码查询课程
     */
    public Course getCourseByCourseCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("课程代码不能为空");
        }
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            CourseMapper mapper = sqlSession.getMapper(CourseMapper.class);
            return mapper.selectByCourseCode(courseCode);
        } catch (Exception e) {
            logger.error("根据课程代码查询课程失败，courseCode：{}", courseCode, e);
            throw new BusinessException("根据课程代码查询课程失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
}
