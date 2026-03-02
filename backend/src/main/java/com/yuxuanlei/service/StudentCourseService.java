package com.yuxuanlei.service;

import com.yuxuanlei.entity.StudentCourse;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.StudentCourseMapper;
import com.yuxuanlei.util.MyBatisUtil;
import com.yuxuanlei.util.ValidationUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 选课管理服务类
 */
public class StudentCourseService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentCourseService.class);
    
    /**
     * 学生选课（M:N 级联）
     */
    public Long enrollCourse(Long studentId, Long courseId) {
        // 输入校验
        ValidationUtil.validateId(studentId, "学生ID");
        ValidationUtil.validateId(courseId, "课程ID");
        
        logger.info("学生选课：studentId={}, courseId={}", studentId, courseId);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            StudentCourseMapper mapper = sqlSession.getMapper(StudentCourseMapper.class);
            
            // 检查是否已选课
            StudentCourse existing = mapper.selectByStudentAndCourse(studentId, courseId);
            if (existing != null) {
                throw new DuplicateDataException("已选修该课程");
            }
            
            StudentCourse sc = new StudentCourse();
            sc.setStudentId(studentId);
            sc.setCourseId(courseId);
            sc.setEnrollDate(LocalDate.now());
            
            mapper.insert(sc);
            sqlSession.commit();
            logger.info("学生选课成功，ID：{}", sc.getId());
            return sc.getId();
        } catch (DuplicateDataException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("学生选课失败：studentId={}, courseId={}", studentId, courseId, e);
            throw new BusinessException("学生选课失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 学生退课
     */
    public boolean dropCourse(Long studentId, Long courseId) {
        // 输入校验
        ValidationUtil.validateId(studentId, "学生ID");
        ValidationUtil.validateId(courseId, "课程ID");
        
        logger.info("学生退课：studentId={}, courseId={}", studentId, courseId);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            StudentCourseMapper mapper = sqlSession.getMapper(StudentCourseMapper.class);
            
            StudentCourse existing = mapper.selectByStudentAndCourse(studentId, courseId);
            if (existing == null) {
                throw new DataNotFoundException("选课记录不存在");
            }
            
            int rows = mapper.deleteByStudentAndCourse(studentId, courseId);
            sqlSession.commit();
            logger.info("学生退课成功：studentId={}, courseId={}", studentId, courseId);
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("学生退课失败：studentId={}, courseId={}", studentId, courseId, e);
            throw new BusinessException("学生退课失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 更新成绩
     */
    public boolean updateScore(Long studentId, Long courseId, BigDecimal score) {
        // 输入校验
        ValidationUtil.validateId(studentId, "学生ID");
        ValidationUtil.validateId(courseId, "课程ID");
        ValidationUtil.validateScore(score);
        
        logger.info("更新成绩：studentId={}, courseId={}, score={}", studentId, courseId, score);
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            StudentCourseMapper mapper = sqlSession.getMapper(StudentCourseMapper.class);
            
            StudentCourse sc = mapper.selectByStudentAndCourse(studentId, courseId);
            if (sc == null) {
                throw new DataNotFoundException("选课记录不存在");
            }
            
            sc.setScore(score);
            int rows = mapper.updateById(sc);
            sqlSession.commit();
            logger.info("更新成绩成功：studentId={}, courseId={}", studentId, courseId);
            return rows > 0;
        } catch (DataNotFoundException e) {
            if (sqlSession != null) sqlSession.rollback();
            throw e;
        } catch (Exception e) {
            if (sqlSession != null) sqlSession.rollback();
            logger.error("更新成绩失败：studentId={}, courseId={}", studentId, courseId, e);
            throw new BusinessException("更新成绩失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
    
    /**
     * 查询选课记录
     */
    public StudentCourse getStudentCourse(Long studentId, Long courseId) {
        // 输入校验
        ValidationUtil.validateId(studentId, "学生ID");
        ValidationUtil.validateId(courseId, "课程ID");
        
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSqlSession();
            StudentCourseMapper mapper = sqlSession.getMapper(StudentCourseMapper.class);
            return mapper.selectByStudentAndCourse(studentId, courseId);
        } catch (Exception e) {
            logger.error("查询选课记录失败：studentId={}, courseId={}", studentId, courseId, e);
            throw new BusinessException("查询选课记录失败：" + e.getMessage(), e);
        } finally {
            MyBatisUtil.closeSqlSession(sqlSession);
        }
    }
}
