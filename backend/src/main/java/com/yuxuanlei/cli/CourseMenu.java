package com.yuxuanlei.cli;

import com.yuxuanlei.entity.Course;
import com.yuxuanlei.service.CourseService;
import com.yuxuanlei.util.PageResult;

import java.util.Scanner;

/**
 * 课程管理菜单
 */
public class CourseMenu {
    
    private final CourseService courseService;
    private final Scanner scanner = new Scanner(System.in);
    
    public CourseMenu(CourseService courseService) {
        this.courseService = courseService;
    }
    
    public void show() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        addCourse();
                        break;
                    case "2":
                        updateCourse();
                        break;
                    case "3":
                        deleteCourse();
                        break;
                    case "4":
                        listCourses();
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
    
    private void addCourse() {
        System.out.println("\n=== 新增课程 ===");
        System.out.print("课程名称：");
        String courseName = scanner.nextLine().trim();
        System.out.print("课程代码：");
        String courseCode = scanner.nextLine().trim();
        System.out.print("学分：");
        int credits = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("授课教师：");
        String teacher = scanner.nextLine().trim();
        
        Course course = new Course();
        course.setCourseName(courseName);
        course.setCourseCode(courseCode);
        course.setCredits(credits);
        course.setTeacher(teacher);
        
        Long id = courseService.addCourse(course);
        System.out.println("✓ 新增成功！课程ID：" + id);
    }
    
    private void updateCourse() {
        System.out.println("\n=== 修改课程信息 ===");
        System.out.print("课程ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        Course course = courseService.getCourseById(id);
        if (course == null) {
            System.out.println("✗ 课程不存在！");
            return;
        }
        
        System.out.println("当前信息：" + course.getCourseName() + " | " + course.getTeacher());
        System.out.print("新课程名称（回车跳过）：");
        String courseName = scanner.nextLine().trim();
        if (!courseName.isEmpty()) {
            course.setCourseName(courseName);
        }
        
        System.out.print("新授课教师（回车跳过）：");
        String teacher = scanner.nextLine().trim();
        if (!teacher.isEmpty()) {
            course.setTeacher(teacher);
        }
        
        courseService.updateCourse(course);
        System.out.println("✓ 更新成功！");
    }
    
    private void deleteCourse() {
        System.out.println("\n=== 删除课程 ===");
        System.out.print("课程ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        System.out.print("确认删除？(y/n)：");
        String confirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消");
            return;
        }
        
        courseService.deleteCourse(id);
        System.out.println("✓ 删除成功！");
    }
    
    private void listCourses() {
        System.out.println("\n=== 课程列表（分页） ===");
        System.out.print("页码（默认1）：");
        String pageStr = scanner.nextLine().trim();
        int pageNum = pageStr.isEmpty() ? 1 : Integer.parseInt(pageStr);
        
        PageResult<Course> page = courseService.listCourses(pageNum, 10);
        
        System.out.println("\n总记录数：" + page.getTotal() + " | 当前页：" + page.getCurrent() + "/" + page.getPages());
        System.out.println("----------------------------------------");
        page.getRecords().forEach(course -> {
            System.out.println("ID:" + course.getId() + " | " + course.getCourseName()
                    + " | " + course.getCourseCode() + " | 学分:" + course.getCredits()
                    + " | 教师:" + course.getTeacher());
        });
    }
    
    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("    课程管理");
        System.out.println("========================================");
        System.out.println("1. 新增课程");
        System.out.println("2. 修改课程信息");
        System.out.println("3. 删除课程");
        System.out.println("4. 课程列表（分页）");
        System.out.println("0. 返回主菜单");
        System.out.println("========================================");
        System.out.print("请选择：");
    }
}
