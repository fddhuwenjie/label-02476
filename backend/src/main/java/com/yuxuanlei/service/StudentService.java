package com.yuxuanlei.service;

import com.yuxuanlei.entity.Student;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.mapper.StudentMapper;
import com.yuxuanlei.util.PageResult;
import com.yuxuanlei.util.TransactionTemplate;
import com.yuxuanlei.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 学生服务类
 */
public class StudentService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    
    /**
     * 新增学生
     */
    public Long addStudent(Student student) {
        ValidationUtil.validateStudent(student);
        logger.info("新增学生：{}", student.getName());
        try {
            return TransactionTemplate.executeWrite(session -> {
                StudentMapper mapper = session.getMapper(StudentMapper.class);
                Student existing = mapper.selectByStudentNo(student.getStudentNo());
                if (existing != null) {
                    throw new DuplicateDataException("学号 " + student.getStudentNo() + " 已存在");
                }
                mapper.insert(student);
                logger.info("新增学生成功，ID：{}", student.getId());
                return student.getId();
            });
        } catch (DuplicateDataException e) {
            throw e;
        } catch (Exception e) {
            logger.error("新增学生失败：{}", student.getName(), e);
            throw new BusinessException("新增学生失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 更新学生信息
     */
    public boolean updateStudent(Student student) {
        ValidationUtil.validateId(student.getId(), "学生ID");
        ValidationUtil.validateStudent(student);
        logger.info("更新学生信息：ID={}", student.getId());
        try {
            return TransactionTemplate.executeWrite(session -> {
                StudentMapper mapper = session.getMapper(StudentMapper.class);
                Student existing = mapper.selectById(student.getId());
                if (existing == null) {
                    throw new DataNotFoundException("学生不存在，ID：" + student.getId());
                }
                int rows = mapper.updateById(student);
                logger.info("更新学生成功，ID：{}", student.getId());
                return rows > 0;
            });
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("更新学生失败，ID：{}", student.getId(), e);
            throw new BusinessException("更新学生失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 删除学生（级联删除借书证、借阅记录、选课记录）
     */
    public boolean deleteStudent(Long id) {
        ValidationUtil.validateId(id, "学生ID");
        logger.info("删除学生：ID={}", id);
        try {
            return TransactionTemplate.executeWrite(session -> {
                StudentMapper mapper = session.getMapper(StudentMapper.class);
                Student existing = mapper.selectById(id);
                if (existing == null) {
                    throw new DataNotFoundException("学生不存在，ID：" + id);
                }
                int rows = mapper.deleteById(id);
                logger.info("删除学生成功，ID：{}（级联删除相关数据）", id);
                return rows > 0;
            });
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("删除学生失败，ID：{}", id, e);
            throw new BusinessException("删除学生失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 根据ID查询学生
     */
    public Student getStudentById(Long id) {
        ValidationUtil.validateId(id, "学生ID");
        try {
            return TransactionTemplate.executeRead(session ->
                    session.getMapper(StudentMapper.class).selectById(id));
        } catch (Exception e) {
            logger.error("查询学生失败，ID：{}", id, e);
            throw new BusinessException("查询学生失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 查询学生详情（含级联信息）
     * <p>一次性加载：借书证(1:1)、借阅记录(1:N 含图书)、选课记录(M:N 含课程及成绩)
     *
     * @param id 学生ID
     * @return 含级联信息的学生对象
     * @throws DataNotFoundException 学生不存在
     */
    public Student getStudentWithDetails(Long id) {
        ValidationUtil.validateId(id, "学生ID");
        logger.info("查询学生详情：ID={}", id);
        try {
            Student student = TransactionTemplate.executeRead(session ->
                    session.getMapper(StudentMapper.class).selectStudentWithDetails(id));
            if (student == null) {
                throw new DataNotFoundException("学生不存在，ID：" + id);
            }
            return student;
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询学生详情失败，ID：{}", id, e);
            throw new BusinessException("查询学生详情失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 分页查询学生列表
     */
    public PageResult<Student> listStudents(int pageNum, int pageSize) {
        ValidationUtil.validatePageParams(pageNum, pageSize);
        try {
            return TransactionTemplate.executeRead(session -> {
                StudentMapper mapper = session.getMapper(StudentMapper.class);
                int offset = (pageNum - 1) * pageSize;
                List<Student> records = mapper.selectList(offset, pageSize);
                long total = mapper.selectCount();
                return new PageResult<>(records, total, pageNum, pageSize);
            });
        } catch (Exception e) {
            logger.error("分页查询学生失败，pageNum：{}，pageSize：{}", pageNum, pageSize, e);
            throw new BusinessException("分页查询学生失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 条件检索学生
     */
    public PageResult<Student> searchStudents(int pageNum, int pageSize, String name, String studentNo, String major) {
        ValidationUtil.validatePageParams(pageNum, pageSize);
        try {
            return TransactionTemplate.executeRead(session -> {
                StudentMapper mapper = session.getMapper(StudentMapper.class);
                int offset = (pageNum - 1) * pageSize;
                List<Student> records = mapper.searchStudents(name, studentNo, major, offset, pageSize);
                long total = mapper.searchCount(name, studentNo, major);
                return new PageResult<>(records, total, pageNum, pageSize);
            });
        } catch (Exception e) {
            logger.error("条件检索学生失败", e);
            throw new BusinessException("条件检索学生失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 根据学号查询学生
     */
    public Student getStudentByNo(String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        try {
            return TransactionTemplate.executeRead(session ->
                    session.getMapper(StudentMapper.class).selectByStudentNo(studentNo));
        } catch (Exception e) {
            logger.error("根据学号查询学生失败，studentNo：{}", studentNo, e);
            throw new BusinessException("根据学号查询学生失败：" + e.getMessage(), e);
        }
    }
}
