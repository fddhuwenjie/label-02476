package com.yuxuanlei.cli;

import com.yuxuanlei.service.*;

import java.util.Scanner;

/**
 * 主菜单
 */
public class MainMenu {
    
    private final StudentMenu studentMenu;
    private final LibraryCardMenu libraryCardMenu;
    private final BookMenu bookMenu;
    private final BookBorrowMenu bookBorrowMenu;
    private final CourseMenu courseMenu;
    private final StudentCourseMenu studentCourseMenu;
    private final QueryMenu queryMenu;
    
    private final Scanner scanner = new Scanner(System.in);
    
    public MainMenu() {
        // 初始化所有服务
        StudentService studentService = new StudentService();
        LibraryCardService libraryCardService = new LibraryCardService();
        BookService bookService = new BookService();
        BookBorrowService bookBorrowService = new BookBorrowService(bookService);
        CourseService courseService = new CourseService();
        StudentCourseService studentCourseService = new StudentCourseService();
        
        // 初始化所有菜单
        this.studentMenu = new StudentMenu(studentService);
        this.libraryCardMenu = new LibraryCardMenu(libraryCardService);
        this.bookMenu = new BookMenu(bookService);
        this.bookBorrowMenu = new BookBorrowMenu(bookBorrowService);
        this.courseMenu = new CourseMenu(courseService);
        this.studentCourseMenu = new StudentCourseMenu(studentCourseService, courseService);
        this.queryMenu = new QueryMenu(studentService);
    }
    
    public void show() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        studentMenu.show();
                        break;
                    case "2":
                        libraryCardMenu.show();
                        break;
                    case "3":
                        bookMenu.show();
                        break;
                    case "4":
                        bookBorrowMenu.show();
                        break;
                    case "5":
                        courseMenu.show();
                        break;
                    case "6":
                        studentCourseMenu.show();
                        break;
                    case "7":
                        queryMenu.show();
                        break;
                    case "0":
                        System.out.println("感谢使用图书管理系统，再见！");
                        System.exit(0);
                        return;
                    default:
                        System.out.println("无效选项，请重新选择！");
                }
            } catch (Exception e) {
                System.out.println(CliErrorHandler.getUserFriendlyMessage(e));
            }
        }
    }
    
    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("    图书管理系统 v1.0");
        System.out.println("========================================");
        System.out.println("1. 学生管理");
        System.out.println("2. 借书证管理");
        System.out.println("3. 图书管理");
        System.out.println("4. 借阅管理");
        System.out.println("5. 课程管理");
        System.out.println("6. 选课管理");
        System.out.println("7. 综合查询（级联查询）");
        System.out.println("0. 退出系统");
        System.out.println("========================================");
        System.out.print("请选择功能模块：");
    }
}
