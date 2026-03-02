package com.yuxuanlei.service;

import com.yuxuanlei.entity.Student;
import com.yuxuanlei.util.PageResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 学生服务测试类
 */
class StudentServiceTest {
    
    private StudentService studentService;
    
    @BeforeEach
    void setUp() {
        studentService = new StudentService();
    }
    
    @Test
    void testAddStudent() {
        Student student = new Student();
        student.setName("测试学生");
        student.setStudentNo("TEST" + System.currentTimeMillis());
        student.setAge(20);
        student.setMajor("软件工程");
        
        Long id = studentService.addStudent(student);
        assertNotNull(id);
        assertTrue(id > 0);
        
        // 清理
        studentService.deleteStudent(id);
    }
    
    @Test
    void testUpdateStudent() {
        // 先新增
        Student student = new Student();
        student.setName("原名");
        student.setStudentNo("TEST" + System.currentTimeMillis());
        student.setAge(20);
        student.setMajor("计算机");
        Long id = studentService.addStudent(student);
        
        // 再更新
        student.setName("新名");
        student.setAge(21);
        boolean result = studentService.updateStudent(student);
        assertTrue(result);
        
        // 验证
        Student updated = studentService.getStudentById(id);
        assertEquals("新名", updated.getName());
        assertEquals(21, updated.getAge());
        
        // 清理
        studentService.deleteStudent(id);
    }
    
    @Test
    void testDeleteStudent() {
        Student student = new Student();
        student.setName("待删除");
        student.setStudentNo("TEST" + System.currentTimeMillis());
        Long id = studentService.addStudent(student);
        
        boolean result = studentService.deleteStudent(id);
        assertTrue(result);
        
        Student deleted = studentService.getStudentById(id);
        assertNull(deleted);
    }
    
    @Test
    void testListStudents() {
        PageResult<Student> page = studentService.listStudents(1, 10);
        assertNotNull(page);
        assertTrue(page.getTotal() >= 0);
    }
    
    @Test
    void testSearchStudents() {
        PageResult<Student> page = studentService.searchStudents(1, 10, "俞轩磊", null, null);
        assertNotNull(page);
    }
}
