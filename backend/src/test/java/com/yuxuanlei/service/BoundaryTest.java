package com.yuxuanlei.service;

import com.yuxuanlei.entity.*;
import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;
import com.yuxuanlei.util.PageResult;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 边界情况测试类
 * 测试空值、非法参数、分页边界、字符串长度、数值范围等
 */
class BoundaryTest {

    private StudentService studentService;
    private BookService bookService;
    private BookBorrowService bookBorrowService;
    private CourseService courseService;
    private StudentCourseService studentCourseService;
    private LibraryCardService libraryCardService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService();
        bookService = new BookService();
        bookBorrowService = new BookBorrowService(bookService);
        courseService = new CourseService();
        studentCourseService = new StudentCourseService();
        libraryCardService = new LibraryCardService();
    }

    // ========== 学生相关边界测试 ==========

    @Test
    void testAddStudent_withNull_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(null));
    }

    @Test
    void testAddStudent_withEmptyName_throwsException() {
        Student student = new Student();
        student.setName("");
        student.setStudentNo("TEST_" + System.currentTimeMillis());
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    void testAddStudent_withWhitespaceName_throwsException() {
        Student student = new Student();
        student.setName("   ");
        student.setStudentNo("TEST_" + System.currentTimeMillis() + "A");
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    void testAddStudent_withNullStudentNo_throwsException() {
        Student student = new Student();
        student.setName("测试");
        student.setStudentNo(null);
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    void testAddStudent_withMajorTooLong_throwsException() {
        Student student = new Student();
        student.setName("测试");
        student.setStudentNo("MAJOR_" + System.currentTimeMillis());
        student.setMajor("a".repeat(101));
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    void testAddStudent_withNameTooLong_throwsException() {
        Student student = new Student();
        student.setName("a".repeat(51));
        student.setStudentNo("TEST_" + System.currentTimeMillis());
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    void testAddStudent_withInvalidAge_throwsException() {
        Student student = new Student();
        student.setName("测试");
        student.setStudentNo("TEST_" + System.currentTimeMillis());
        student.setAge(0);
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    void testAddStudent_withAgeOutOfRange_throwsException() {
        Student student = new Student();
        student.setName("测试");
        student.setStudentNo("TEST_" + System.currentTimeMillis());
        student.setAge(151);
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    void testAddStudent_duplicateStudentNo_throwsException() {
        Student student = new Student();
        student.setName("重复学号测试");
        student.setStudentNo("DUP_NO_" + System.currentTimeMillis());
        Long id = studentService.addStudent(student);
        assertNotNull(id);

        Student duplicate = new Student();
        duplicate.setName("另一个");
        duplicate.setStudentNo(student.getStudentNo());
        assertThrows(DuplicateDataException.class, () -> studentService.addStudent(duplicate));

        studentService.deleteStudent(id);
    }

    @Test
    void testGetStudentById_withInvalidId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> studentService.getStudentById(null));
        assertThrows(IllegalArgumentException.class, () -> studentService.getStudentById(0L));
        assertThrows(IllegalArgumentException.class, () -> studentService.getStudentById(-1L));
    }

    @Test
    void testGetStudentWithDetails_nonExistent_throwsException() {
        assertThrows(DataNotFoundException.class, () -> studentService.getStudentWithDetails(999999L));
    }

    @Test
    void testDeleteStudent_withNullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> studentService.deleteStudent(null));
    }

    @Test
    void testDeleteStudent_withZeroId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> studentService.deleteStudent(0L));
    }

    @Test
    void testGetStudentByNo_withNull_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> studentService.getStudentByNo(null));
    }

    @Test
    void testGetStudentByNo_withEmpty_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> studentService.getStudentByNo(""));
        assertThrows(IllegalArgumentException.class, () -> studentService.getStudentByNo("   "));
    }

    // ========== 分页参数边界测试 ==========

    @Test
    void testListStudents_invalidPageNum_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> studentService.listStudents(0, 10));
        assertThrows(IllegalArgumentException.class, () -> studentService.listStudents(-1, 10));
    }

    @Test
    void testListStudents_invalidPageSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> studentService.listStudents(1, 0));
        assertThrows(IllegalArgumentException.class, () -> studentService.listStudents(1, 101));
    }

    @Test
    void testListStudents_validBoundaries_succeeds() {
        PageResult<Student> page = studentService.listStudents(1, 1);
        assertNotNull(page);
        assertEquals(1, page.getSize());
        assertTrue(page.getTotal() >= 0);

        page = studentService.listStudents(1, 100);
        assertNotNull(page);
        assertEquals(100, page.getSize());
    }

    @Test
    void testListStudents_pageBeyondTotal_returnsEmpty() {
        // 请求一个很大的页码，应返回空列表而非抛异常
        PageResult<Student> page = studentService.listStudents(999999, 10);
        assertNotNull(page);
        assertTrue(page.getRecords().isEmpty());
        assertTrue(page.getTotal() >= 0);
    }

    // ========== 图书相关边界测试 ==========

    @Test
    void testAddBook_withNegativeTotalCount_throwsException() {
        Book book = new Book();
        book.setBookName("测试图书");
        book.setTotalCount(-1);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book));
    }

    @Test
    void testAddBook_withAvailableGreaterThanTotal_throwsException() {
        Book book = new Book();
        book.setBookName("测试图书");
        book.setTotalCount(5);
        book.setAvailableCount(10);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book));
    }

    @Test
    void testAddBook_withEmptyName_throwsException() {
        Book book = new Book();
        book.setBookName("");
        book.setTotalCount(5);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book));
    }

    @Test
    void testAddBook_withNameTooLong_throwsException() {
        Book book = new Book();
        book.setBookName("a".repeat(201));
        book.setAuthor("作者");
        book.setIsbn("LEN_" + System.currentTimeMillis());
        book.setTotalCount(1);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book));
    }

    @Test
    void testAddBook_withAuthorTooLong_throwsException() {
        Book book = new Book();
        book.setBookName("测试图书");
        book.setAuthor("a".repeat(101));
        book.setIsbn("AUTH_" + System.currentTimeMillis());
        book.setTotalCount(1);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book));
    }

    @Test
    void testAddBook_withIsbnTooLong_throwsException() {
        Book book = new Book();
        book.setBookName("测试图书");
        book.setAuthor("作者");
        book.setIsbn("a".repeat(21));
        book.setTotalCount(1);
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(book));
    }

    @Test
    void testBorrowBook_withNullStudentId_throwsException() {
        BookBorrow borrow = new BookBorrow();
        borrow.setStudentId(null);
        borrow.setBookId(1L);
        assertThrows(IllegalArgumentException.class, () -> bookBorrowService.borrowBook(borrow));
    }

    @Test
    void testBorrowBook_withNullBookId_throwsException() {
        BookBorrow borrow = new BookBorrow();
        borrow.setStudentId(1L);
        borrow.setBookId(null);
        assertThrows(IllegalArgumentException.class, () -> bookBorrowService.borrowBook(borrow));
    }

    @Test
    void testDecreaseAvailableCount_whenZero_throwsException() {
        Book book = new Book();
        book.setBookName("零库存测试");
        book.setAuthor("作者");
        book.setIsbn("ZERO_" + System.currentTimeMillis());
        book.setTotalCount(1);
        Long id = bookService.addBook(book);

        bookService.decreaseAvailableCount(id); // 1 -> 0, OK
        assertThrows(BusinessException.class, () -> bookService.decreaseAvailableCount(id)); // 0 库存，应失败

        bookService.deleteBook(id);
    }

    // ========== 课程相关边界测试 ==========

    @Test
    void testAddCourse_withInvalidCredits_throwsException() {
        Course course = new Course();
        course.setCourseName("测试课程");
        course.setCourseCode("CRED_" + System.currentTimeMillis());
        course.setCredits(11);
        assertThrows(IllegalArgumentException.class, () -> courseService.addCourse(course));
    }

    @Test
    void testAddCourse_withNegativeCredits_throwsException() {
        Course course = new Course();
        course.setCourseName("测试课程");
        course.setCourseCode("CRED2_" + System.currentTimeMillis());
        course.setCredits(-1);
        assertThrows(IllegalArgumentException.class, () -> courseService.addCourse(course));
    }

    @Test
    void testAddCourse_withEmptyCourseName_throwsException() {
        Course course = new Course();
        course.setCourseName("");
        course.setCourseCode("EMP_" + System.currentTimeMillis());
        assertThrows(IllegalArgumentException.class, () -> courseService.addCourse(course));
    }

    @Test
    void testAddCourse_withCourseNameTooLong_throwsException() {
        Course course = new Course();
        course.setCourseName("a".repeat(101));
        course.setCourseCode("LEN_" + System.currentTimeMillis());
        assertThrows(IllegalArgumentException.class, () -> courseService.addCourse(course));
    }

    @Test
    void testAddCourse_withCourseCodeTooLong_throwsException() {
        Course course = new Course();
        course.setCourseName("测试课程");
        course.setCourseCode("a".repeat(21));
        assertThrows(IllegalArgumentException.class, () -> courseService.addCourse(course));
    }

    @Test
    void testAddCourse_withTeacherTooLong_throwsException() {
        Course course = new Course();
        course.setCourseName("测试课程");
        course.setCourseCode("TCH_" + System.currentTimeMillis());
        course.setTeacher("a".repeat(51));
        assertThrows(IllegalArgumentException.class, () -> courseService.addCourse(course));
    }

    @Test
    void testUpdateScore_invalidScore_throwsException() {
        Student student = new Student();
        student.setName("成绩测试");
        student.setStudentNo("SCORE_" + System.currentTimeMillis());
        Long studentId = studentService.addStudent(student);

        Course course = new Course();
        course.setCourseName("成绩课程");
        course.setCourseCode("SC_" + System.currentTimeMillis());
        Long courseId = courseService.addCourse(course);

        studentCourseService.enrollCourse(studentId, courseId);

        assertThrows(IllegalArgumentException.class,
                () -> studentCourseService.updateScore(studentId, courseId, new BigDecimal("101")));
        assertThrows(IllegalArgumentException.class,
                () -> studentCourseService.updateScore(studentId, courseId, new BigDecimal("-0.01")));

        studentService.deleteStudent(studentId);
        courseService.deleteCourse(courseId);
    }

    // ========== 借书证边界测试 ==========

    @Test
    void testUpdateCardStatus_withInvalidStatus_throwsException() {
        String base = "STAT_" + System.currentTimeMillis();
        Student student = new Student();
        student.setName("状态测试");
        student.setStudentNo(base);
        Long studentId = studentService.addStudent(student);

        LibraryCard card = new LibraryCard();
        card.setStudentId(studentId);
        card.setCardNo(base + "_C");
        Long cardId = libraryCardService.issueCard(card);
        assertThrows(IllegalArgumentException.class, () -> libraryCardService.updateCardStatus(cardId, "INVALID"));

        studentService.deleteStudent(studentId);
    }

    @Test
    void testIssueCard_withCardNoTooLong_throwsException() {
        Student student = new Student();
        student.setName("卡号测试");
        student.setStudentNo("CNO_" + System.currentTimeMillis());
        Long studentId = studentService.addStudent(student);

        LibraryCard card = new LibraryCard();
        card.setStudentId(studentId);
        card.setCardNo("a".repeat(31));
        assertThrows(IllegalArgumentException.class, () -> libraryCardService.issueCard(card));

        studentService.deleteStudent(studentId);
    }

    @Test
    void testIssueCard_duplicateCardForSameStudent_throwsException() {
        String base = "CARD_" + System.currentTimeMillis();
        Student student = new Student();
        student.setName("借书证测试");
        student.setStudentNo(base);
        Long studentId = studentService.addStudent(student);

        LibraryCard card1 = new LibraryCard();
        card1.setStudentId(studentId);
        card1.setCardNo(base + "_1");
        libraryCardService.issueCard(card1);

        LibraryCard card2 = new LibraryCard();
        card2.setStudentId(studentId);
        card2.setCardNo(base + "_2");
        assertThrows(DuplicateDataException.class, () -> libraryCardService.issueCard(card2));

        studentService.deleteStudent(studentId);
    }

    // ========== 选课重复测试 ==========

    @Test
    void testEnrollCourse_duplicateEnrollment_throwsException() {
        Student student = new Student();
        student.setName("选课重复测试");
        student.setStudentNo("ENR_" + System.currentTimeMillis());
        Long studentId = studentService.addStudent(student);

        Course course = new Course();
        course.setCourseName("重复选课测试");
        course.setCourseCode("ENR_" + System.currentTimeMillis());
        Long courseId = courseService.addCourse(course);

        studentCourseService.enrollCourse(studentId, courseId);
        assertThrows(DuplicateDataException.class, () -> studentCourseService.enrollCourse(studentId, courseId));

        studentService.deleteStudent(studentId);
        courseService.deleteCourse(courseId);
    }
}
