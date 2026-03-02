package com.yuxuanlei.service;

import com.yuxuanlei.entity.Student;
import com.yuxuanlei.util.PageResult;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分页查询测试类
 */
class PaginationTest {
    
    private StudentService studentService;
    private List<Long> createdStudentIds;
    
    @BeforeEach
    void setUp() {
        studentService = new StudentService();
        createdStudentIds = new ArrayList<>();
    }
    
    @AfterEach
    void tearDown() {
        // 清理测试数据
        for (Long id : createdStudentIds) {
            try {
                studentService.deleteStudent(id);
            } catch (Exception e) {
                // 忽略删除失败
            }
        }
    }
    
    @Test
    void testStudentPagination() {
        // 添加测试数据
        for (int i = 1; i <= 15; i++) {
            Student student = new Student();
            student.setName("分页测试学生" + i);
            student.setStudentNo("PAGE" + System.currentTimeMillis() + i);
            student.setAge(20);
            student.setMajor("计算机");
            Long id = studentService.addStudent(student);
            createdStudentIds.add(id);
        }
        
        // 测试第一页
        PageResult<Student> page1 = studentService.listStudents(1, 10);
        assertTrue(page1.getRecords().size() <= 10);
        assertTrue(page1.getTotal() >= 15);
        
        // 测试第二页
        PageResult<Student> page2 = studentService.listStudents(2, 10);
        assertNotNull(page2.getRecords());
    }
    
    @Test
    @Disabled("等待 BookService 和 CourseService 重构完成")
    void testBookAndCoursePagination() {
        // 此测试需要 BookService 和 CourseService 重构完成后才能运行
    }
}
