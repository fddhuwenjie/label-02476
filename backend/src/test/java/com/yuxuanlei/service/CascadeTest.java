package com.yuxuanlei.service;

import com.yuxuanlei.entity.*;
import com.yuxuanlei.exception.DuplicateDataException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * 级联关系测试类
 * 使用 TestDataCleanup 统一清理，支持 @AfterEach 与 ShutdownHook 兜底（测试中断时尽量清理残留）
 */
class CascadeTest {

    /** 静态注册表：ShutdownHook 可访问，测试中断时兜底清理 */
    private static final List<Long> PENDING_STUDENT_IDS = new CopyOnWriteArrayList<>();
    private static final List<Long> PENDING_BOOK_IDS = new CopyOnWriteArrayList<>();
    private static final List<Long> PENDING_COURSE_IDS = new CopyOnWriteArrayList<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Long id : PENDING_STUDENT_IDS) {
                try { new StudentService().deleteStudent(id); } catch (Exception ignored) {}
            }
            for (Long id : PENDING_BOOK_IDS) {
                try { new BookService().deleteBook(id); } catch (Exception ignored) {}
            }
            for (Long id : PENDING_COURSE_IDS) {
                try { new CourseService().deleteCourse(id); } catch (Exception ignored) {}
            }
        }));
    }

    private StudentService studentService;
    private LibraryCardService libraryCardService;
    private BookService bookService;
    private BookBorrowService bookBorrowService;
    private CourseService courseService;
    private StudentCourseService studentCourseService;
    private List<Long> createdStudentIds;
    private List<Long> createdBookIds;
    private List<Long> createdCourseIds;

    @BeforeEach
    void setUp() {
        studentService = new StudentService();
        libraryCardService = new LibraryCardService();
        bookService = new BookService();
        bookBorrowService = new BookBorrowService(bookService);
        courseService = new CourseService();
        studentCourseService = new StudentCourseService();
        createdStudentIds = new ArrayList<>();
        createdBookIds = new ArrayList<>();
        createdCourseIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        // 先删学生（级联借书证、借阅、选课），再删图书、课程
        for (Long id : createdStudentIds) {
            try {
                studentService.deleteStudent(id);
                PENDING_STUDENT_IDS.remove(id);
            } catch (Exception ignored) {}
        }
        for (Long id : createdBookIds) {
            try {
                bookService.deleteBook(id);
                PENDING_BOOK_IDS.remove(id);
            } catch (Exception ignored) {}
        }
        for (Long id : createdCourseIds) {
            try {
                courseService.deleteCourse(id);
                PENDING_COURSE_IDS.remove(id);
            } catch (Exception ignored) {}
        }
    }
    
    /**
     * 测试 1:1 级联（学生-借书证）
     */
    @Test
    void testOneToOneCascade() {
        String base = "C1_" + System.currentTimeMillis();
        Student student = new Student();
        student.setName("测试学生A");
        student.setStudentNo(base);
        student.setAge(20);
        student.setMajor("计算机");
        Long studentId = studentService.addStudent(student);
        createdStudentIds.add(studentId);
        PENDING_STUDENT_IDS.add(studentId);

        String cardNo = base + "_CARD";
        LibraryCard card = new LibraryCard();
        card.setStudentId(studentId);
        card.setCardNo(cardNo);
        Long cardId = libraryCardService.issueCard(card);
        
        assertNotNull(cardId);
        
        LibraryCard queriedCard = libraryCardService.getCardByStudentId(studentId);
        assertNotNull(queriedCard);
        assertEquals(cardNo, queriedCard.getCardNo());
        
        LibraryCard duplicateCard = new LibraryCard();
        duplicateCard.setStudentId(studentId);
        duplicateCard.setCardNo(base + "_CARD2");
        
        assertThrows(DuplicateDataException.class, () -> {
            libraryCardService.issueCard(duplicateCard);
        });
    }
    
    /**
     * 测试 1:N 级联（学生-借阅记录）
     */
    @Test
    void testOneToManyCascade() {
        String base = "C2_" + System.currentTimeMillis();
        Student student = new Student();
        student.setName("测试学生B");
        student.setStudentNo(base);
        Long studentId = studentService.addStudent(student);
        createdStudentIds.add(studentId);
        PENDING_STUDENT_IDS.add(studentId);

        Book book1 = new Book();
        book1.setBookName("测试图书1");
        book1.setAuthor("作者1");
        book1.setIsbn("ISBN_" + base + "_1");
        book1.setTotalCount(5);
        Long bookId1 = bookService.addBook(book1);
        createdBookIds.add(bookId1);
        PENDING_BOOK_IDS.add(bookId1);

        Book book2 = new Book();
        book2.setBookName("测试图书2");
        book2.setAuthor("作者2");
        book2.setIsbn("ISBN_" + base + "_2");
        book2.setTotalCount(3);
        Long bookId2 = bookService.addBook(book2);
        createdBookIds.add(bookId2);
        PENDING_BOOK_IDS.add(bookId2);

        // 3. 学生借多本书（1:N 级联）
        BookBorrow borrow1 = new BookBorrow();
        borrow1.setStudentId(studentId);
        borrow1.setBookId(bookId1);
        bookBorrowService.borrowBook(borrow1);
        
        BookBorrow borrow2 = new BookBorrow();
        borrow2.setStudentId(studentId);
        borrow2.setBookId(bookId2);
        bookBorrowService.borrowBook(borrow2);
        
        // 4. 验证级联查询
        List<BookBorrow> borrows = bookBorrowService.getBorrowsByStudentId(studentId);
        assertEquals(2, borrows.size());
        
        // 5. 验证图书库存减少
        Book queriedBook1 = bookService.getBookById(bookId1);
        assertEquals(4, queriedBook1.getAvailableCount());
    }
    
    /**
     * 测试 M:N 级联（学生-课程）
     */
    @Test
    void testManyToManyCascade() {
        String base = "C3_" + System.currentTimeMillis();
        Student student1 = new Student();
        student1.setName("测试学生C");
        student1.setStudentNo(base + "A");
        Long studentId1 = studentService.addStudent(student1);
        createdStudentIds.add(studentId1);
        PENDING_STUDENT_IDS.add(studentId1);

        Student student2 = new Student();
        student2.setName("测试学生D");
        student2.setStudentNo(base + "B");
        Long studentId2 = studentService.addStudent(student2);
        createdStudentIds.add(studentId2);
        PENDING_STUDENT_IDS.add(studentId2);

        Course course1 = new Course();
        course1.setCourseName("测试课程1");
        course1.setCourseCode("CRS_" + base + "_1");
        course1.setCredits(4);
        course1.setTeacher("教师1");
        Long courseId1 = courseService.addCourse(course1);
        createdCourseIds.add(courseId1);
        PENDING_COURSE_IDS.add(courseId1);

        Course course2 = new Course();
        course2.setCourseName("测试课程2");
        course2.setCourseCode("CRS_" + base + "_2");
        course2.setCredits(3);
        course2.setTeacher("教师2");
        Long courseId2 = courseService.addCourse(course2);
        createdCourseIds.add(courseId2);
        PENDING_COURSE_IDS.add(courseId2);

        // 3. 学生选课（M:N 级联）
        studentCourseService.enrollCourse(studentId1, courseId1);
        studentCourseService.enrollCourse(studentId1, courseId2);
        studentCourseService.enrollCourse(studentId2, courseId1);
        
        // 4. 验证级联查询（选课记录含课程及成绩）
        List<StudentCourse> student1Enrollments = courseService.getCourseEnrollmentsByStudentId(studentId1);
        assertEquals(2, student1Enrollments.size());
        
        List<StudentCourse> student2Enrollments = courseService.getCourseEnrollmentsByStudentId(studentId2);
        assertEquals(1, student2Enrollments.size());
        
        // 5. 测试更新成绩
        studentCourseService.updateScore(studentId1, courseId1, new BigDecimal("95.5"));
        StudentCourse sc = studentCourseService.getStudentCourse(studentId1, courseId1);
        assertEquals(new BigDecimal("95.50"), sc.getScore());
        
        // 6. 测试退课
        studentCourseService.dropCourse(studentId1, courseId2);
        List<StudentCourse> afterDrop = courseService.getCourseEnrollmentsByStudentId(studentId1);
        assertEquals(1, afterDrop.size());
    }
    
    /**
     * 测试级联删除
     */
    @Test
    void testCascadeDelete() {
        String base = "C5_" + System.currentTimeMillis();
        Student student = new Student();
        student.setName("测试学生E");
        student.setStudentNo(base);
        Long studentId = studentService.addStudent(student);
        createdStudentIds.add(studentId);
        PENDING_STUDENT_IDS.add(studentId);

        LibraryCard card = new LibraryCard();
        card.setStudentId(studentId);
        card.setCardNo(base + "_CARD");
        libraryCardService.issueCard(card);
        
        Book book = new Book();
        book.setBookName("测试图书");
        book.setAuthor("作者");
        book.setIsbn("ISBN_" + base);
        book.setTotalCount(5);
        Long bookId = bookService.addBook(book);
        createdBookIds.add(bookId);
        PENDING_BOOK_IDS.add(bookId);

        BookBorrow borrow = new BookBorrow();
        borrow.setStudentId(studentId);
        borrow.setBookId(bookId);
        bookBorrowService.borrowBook(borrow);
        
        Course course = new Course();
        course.setCourseName("测试课程");
        course.setCourseCode("CRS_" + base);
        course.setCredits(4);
        Long courseId = courseService.addCourse(course);
        createdCourseIds.add(courseId);
        PENDING_COURSE_IDS.add(courseId);
        studentCourseService.enrollCourse(studentId, courseId);

        // 2. 删除学生（级联删除）
        studentService.deleteStudent(studentId);
        
        // 3. 验证级联删除
        assertNull(studentService.getStudentById(studentId));
        assertNull(libraryCardService.getCardByStudentId(studentId));
        assertTrue(bookBorrowService.getBorrowsByStudentId(studentId).isEmpty());
        assertTrue(courseService.getCourseEnrollmentsByStudentId(studentId).isEmpty());
    }
    
    /**
     * 测试完整级联查询（依赖 schema 预置数据：俞轩磊 2024001）
     * 若数据库未执行 schema.sql，则跳过本测试
     */
    @Test
    void testFullCascadeQuery() {
        Student yu;
        try {
            yu = studentService.getStudentByNo("2024001");
        } catch (Exception e) {
            assumeTrue(false, "数据库未初始化或连接失败，请先执行 schema.sql。cd backend 后运行 ./init-db.sh 或 .\\init-db.ps1；Docker 首次启动会自动执行。原因：" + e.getMessage());
            return;
        }
        assumeTrue(yu != null, "预置数据俞轩磊(学号2024001)不存在，请执行 schema.sql 初始化");
        Student student = studentService.getStudentWithDetails(yu.getId());
        
        assertNotNull(student);
        assertEquals("俞轩磊", student.getName());
        
        // 验证 1:1 借书证
        assertNotNull(student.getLibraryCard());
        
        // 验证 1:N 借阅记录
        assertNotNull(student.getBookBorrows());
        // 验证 1:N 所借图书（Prompt 要求的直接 List<Book> 级联）
        assertNotNull(student.getBorrowedBooks());
        
        // 验证 M:N 选课记录（score 在 StudentCourse 中）
        assertNotNull(student.getCourseEnrollments());
    }
}
