package com.yuxuanlei.cli;

import com.yuxuanlei.entity.Course;
import com.yuxuanlei.entity.StudentCourse;
import com.yuxuanlei.service.CourseService;
import com.yuxuanlei.service.StudentCourseService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * 选课管理菜单
 */
public class StudentCourseMenu {
    
    private final StudentCourseService studentCourseService;
    private final CourseService courseService;
    private final Scanner scanner = new Scanner(System.in);
    
    public StudentCourseMenu(StudentCourseService studentCourseService, CourseService courseService) {
        this.studentCourseService = studentCourseService;
        this.courseService = courseService;
    }
    
    public void show() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        enrollCourse();
                        break;
                    case "2":
                        dropCourse();
                        break;
                    case "3":
                        updateScore();
                        break;
                    case "4":
                        viewStudentCourses();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("无效选项！");
                }
            } catch (Exception e) {
                System.out.println(CliErrorHandler.getUserFriendlyMessage(e));
            }
        }
    }
    
    private void enrollCourse() {
        System.out.println("\n=== 学生选课（M:N 级联） ===");
        System.out.print("学生ID：");
        Long studentId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("课程ID：");
        Long courseId = Long.parseLong(scanner.nextLine().trim());
        
        Long id = studentCourseService.enrollCourse(studentId, courseId);
        System.out.println("✓ 选课成功！选课记录ID：" + id);
    }
    
    private void dropCourse() {
        System.out.println("\n=== 学生退课 ===");
        System.out.print("学生ID：");
        Long studentId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("课程ID：");
        Long courseId = Long.parseLong(scanner.nextLine().trim());
        
        System.out.print("确认退课？(y/n)：");
        String confirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消");
            return;
        }
        
        studentCourseService.dropCourse(studentId, courseId);
        System.out.println("✓ 退课成功！");
    }
    
    private void updateScore() {
        System.out.println("\n=== 更新成绩 ===");
        System.out.print("学生ID：");
        Long studentId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("课程ID：");
        Long courseId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("成绩：");
        BigDecimal score = new BigDecimal(scanner.nextLine().trim());
        
        studentCourseService.updateScore(studentId, courseId, score);
        System.out.println("✓ 成绩更新成功！");
    }
    
    private void viewStudentCourses() {
        System.out.println("\n=== 查询学生选修课程 ===");
        System.out.print("学生ID：");
        Long studentId = Long.parseLong(scanner.nextLine().trim());
        
        List<StudentCourse> enrollments = courseService.getCourseEnrollmentsByStudentId(studentId);
        
        System.out.println("\n选修课程：" + enrollments.size() + " 门");
        System.out.println("----------------------------------------");
        enrollments.forEach(enrollment -> {
            Course course = enrollment.getCourse();
            if (course != null) {
                System.out.println("ID:" + course.getId() + " | " + course.getCourseName()
                        + " | 学分:" + course.getCredits()
                        + " | 成绩:" + (enrollment.getScore() != null ? enrollment.getScore() : "未录入"));
            }
        });
    }
    
    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("    选课管理");
        System.out.println("========================================");
        System.out.println("1. 学生选课（M:N 级联）");
        System.out.println("2. 学生退课");
        System.out.println("3. 更新成绩");
        System.out.println("4. 查询学生选修课程");
        System.out.println("0. 返回主菜单");
        System.out.println("========================================");
        System.out.print("请选择：");
    }
}
