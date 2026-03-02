package com.yuxuanlei.cli;

import com.yuxuanlei.entity.Course;
import com.yuxuanlei.entity.Student;
import com.yuxuanlei.service.StudentService;

import java.util.Scanner;

/**
 * 综合查询菜单（级联查询）
 */
public class QueryMenu {
    
    private final StudentService studentService;
    private final Scanner scanner = new Scanner(System.in);
    
    public QueryMenu(StudentService studentService) {
        this.studentService = studentService;
    }
    
    public void show() {
        System.out.println("\n=== 综合查询（级联查询） ===");
        System.out.print("学生ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        Student student = studentService.getStudentWithDetails(id);
        if (student == null) {
            System.out.println("✗ 学生不存在！");
            return;
        }
        
        displayStudentDetails(student);
    }
    
    private void displayStudentDetails(Student student) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         学生完整信息（级联查询）         ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        System.out.println("\n【基本信息】");
        System.out.println("  ID：" + student.getId());
        System.out.println("  姓名：" + student.getName());
        System.out.println("  学号：" + student.getStudentNo());
        System.out.println("  年龄：" + student.getAge());
        System.out.println("  专业：" + student.getMajor());
        
        if (student.getLibraryCard() != null) {
            System.out.println("\n【借书证信息】（1:1 级联）");
            System.out.println("  借书证号：" + student.getLibraryCard().getCardNo());
            System.out.println("  状态：" + student.getLibraryCard().getStatus());
            System.out.println("  发证日期：" + student.getLibraryCard().getIssueDate());
            System.out.println("  有效期至：" + student.getLibraryCard().getExpireDate());
        } else {
            System.out.println("\n【借书证信息】（1:1 级联）");
            System.out.println("  暂无借书证");
        }
        
        if (student.getBorrowedBooks() != null && !student.getBorrowedBooks().isEmpty()) {
            System.out.println("\n【所借图书】（1:N 级联：学生-所借图书，Prompt 要求的直接 List<Book>）");
            student.getBorrowedBooks().forEach(book -> {
                System.out.println("  - " + book.getBookName() + " | 作者：" + (book.getAuthor() != null ? book.getAuthor() : "-"));
            });
        } else {
            System.out.println("\n【所借图书】（1:N 级联：学生-所借图书）");
            System.out.println("  暂无借阅");
        }
        
        if (student.getBookBorrows() != null && !student.getBookBorrows().isEmpty()) {
            System.out.println("\n【借阅记录】（1:N 级联：学生-借阅记录）");
            student.getBookBorrows().forEach(borrow -> {
                System.out.println("  - 图书：" + (borrow.getBook() != null ? borrow.getBook().getBookName() : "未知"));
                System.out.println("    状态：" + borrow.getStatus());
                System.out.println("    借书日期：" + borrow.getBorrowDate());
                if (borrow.getReturnDate() != null) {
                    System.out.println("    还书日期：" + borrow.getReturnDate());
                }
                System.out.println();
            });
        } else {
            System.out.println("\n【借阅记录】");
            System.out.println("  暂无借阅记录");
        }
        
        if (student.getCourseEnrollments() != null && !student.getCourseEnrollments().isEmpty()) {
            System.out.println("\n【选修课程】（M:N 级联，成绩在选课记录 StudentCourse 中）");
            student.getCourseEnrollments().forEach(enrollment -> {
                Course course = enrollment.getCourse();
                if (course != null) {
                    System.out.println("  - 课程：" + course.getCourseName());
                    System.out.println("    课程代码：" + course.getCourseCode());
                    System.out.println("    学分：" + course.getCredits());
                    System.out.println("    教师：" + course.getTeacher());
                }
                System.out.println("    成绩：" + (enrollment.getScore() != null ? enrollment.getScore() : "未录入"));
                System.out.println();
            });
        } else {
            System.out.println("\n【选修课程】（M:N 级联）");
            System.out.println("  暂无选课记录");
        }
        
        System.out.println("========================================\n");
    }
}
