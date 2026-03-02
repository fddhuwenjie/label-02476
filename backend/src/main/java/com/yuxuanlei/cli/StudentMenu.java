package com.yuxuanlei.cli;

import com.yuxuanlei.entity.Course;
import com.yuxuanlei.entity.Student;
import com.yuxuanlei.service.StudentService;
import com.yuxuanlei.util.PageResult;

import java.util.Scanner;

/**
 * 学生管理菜单
 */
public class StudentMenu {
    
    private final StudentService studentService;
    private final Scanner scanner = new Scanner(System.in);
    
    public StudentMenu(StudentService studentService) {
        this.studentService = studentService;
    }
    
    public void show() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        addStudent();
                        break;
                    case "2":
                        updateStudent();
                        break;
                    case "3":
                        deleteStudent();
                        break;
                    case "4":
                        viewStudentDetails();
                        break;
                    case "5":
                        listStudents();
                        break;
                    case "6":
                        searchStudents();
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
    
    private void addStudent() {
        System.out.println("\n=== 新增学生 ===");
        System.out.print("姓名：");
        String name = scanner.nextLine().trim();
        System.out.print("学号：");
        String studentNo = scanner.nextLine().trim();
        System.out.print("年龄：");
        int age = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("专业：");
        String major = scanner.nextLine().trim();
        
        Student student = new Student();
        student.setName(name);
        student.setStudentNo(studentNo);
        student.setAge(age);
        student.setMajor(major);
        
        Long id = studentService.addStudent(student);
        System.out.println("✓ 新增成功！学生ID：" + id);
    }
    
    private void updateStudent() {
        System.out.println("\n=== 修改学生信息 ===");
        System.out.print("学生ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        Student student = studentService.getStudentById(id);
        if (student == null) {
            System.out.println("✗ 学生不存在！");
            return;
        }
        
        System.out.println("当前信息：" + student.getName() + " | " + student.getStudentNo());
        System.out.print("新姓名（回车跳过）：");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            student.setName(name);
        }
        
        System.out.print("新年龄（回车跳过）：");
        String ageStr = scanner.nextLine().trim();
        if (!ageStr.isEmpty()) {
            student.setAge(Integer.parseInt(ageStr));
        }
        
        System.out.print("新专业（回车跳过）：");
        String major = scanner.nextLine().trim();
        if (!major.isEmpty()) {
            student.setMajor(major);
        }
        
        studentService.updateStudent(student);
        System.out.println("✓ 更新成功！");
    }
    
    private void deleteStudent() {
        System.out.println("\n=== 删除学生 ===");
        System.out.print("学生ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        System.out.print("确认删除？(y/n)：");
        String confirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消");
            return;
        }
        
        studentService.deleteStudent(id);
        System.out.println("✓ 删除成功！（级联删除借书证、借阅记录、选课记录）");
    }
    
    private void viewStudentDetails() {
        System.out.println("\n=== 查询学生详情（级联查询） ===");
        System.out.print("学生ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        Student student = studentService.getStudentWithDetails(id);
        if (student == null) {
            System.out.println("✗ 学生不存在！");
            return;
        }
        
        System.out.println("\n【学生信息】");
        System.out.println("ID：" + student.getId());
        System.out.println("姓名：" + student.getName());
        System.out.println("学号：" + student.getStudentNo());
        System.out.println("年龄：" + student.getAge());
        System.out.println("专业：" + student.getMajor());
        
        if (student.getLibraryCard() != null) {
            System.out.println("\n【借书证信息】（1:1 级联）");
            System.out.println("借书证号：" + student.getLibraryCard().getCardNo());
            System.out.println("状态：" + student.getLibraryCard().getStatus());
            System.out.println("有效期：" + student.getLibraryCard().getExpireDate());
        }
        
        if (student.getBorrowedBooks() != null && !student.getBorrowedBooks().isEmpty()) {
            System.out.println("\n【所借图书】（1:N 级联）");
            student.getBorrowedBooks().forEach(book -> {
                System.out.println("- " + book.getBookName() + (book.getAuthor() != null ? " | " + book.getAuthor() : ""));
            });
        }
        if (student.getBookBorrows() != null && !student.getBookBorrows().isEmpty()) {
            System.out.println("\n【借阅记录】（1:N 级联）");
            student.getBookBorrows().forEach(borrow -> {
                System.out.println("- " + (borrow.getBook() != null ? borrow.getBook().getBookName() : "未知") 
                        + " | 状态：" + borrow.getStatus() 
                        + " | 借书日期：" + borrow.getBorrowDate());
            });
        }
        
        if (student.getCourseEnrollments() != null && !student.getCourseEnrollments().isEmpty()) {
            System.out.println("\n【选修课程】（M:N 级联）");
            student.getCourseEnrollments().forEach(enrollment -> {
                Course c = enrollment.getCourse();
                if (c != null) {
                    System.out.println("- " + c.getCourseName() 
                            + " | 学分：" + c.getCredits() 
                            + " | 成绩：" + (enrollment.getScore() != null ? enrollment.getScore() : "未录入"));
                }
            });
        }
    }
    
    private void listStudents() {
        System.out.println("\n=== 学生列表（分页） ===");
        System.out.print("页码（默认1）：");
        String pageStr = scanner.nextLine().trim();
        int pageNum = pageStr.isEmpty() ? 1 : Integer.parseInt(pageStr);
        
        PageResult<Student> page = studentService.listStudents(pageNum, 10);
        
        System.out.println("\n总记录数：" + page.getTotal() + " | 当前页：" + page.getCurrent() + "/" + page.getPages());
        System.out.println("----------------------------------------");
        page.getRecords().forEach(s -> {
            System.out.println("ID:" + s.getId() + " | " + s.getName() + " | " + s.getStudentNo() + " | " + s.getMajor());
        });
    }
    
    private void searchStudents() {
        System.out.println("\n=== 条件检索 ===");
        System.out.print("姓名（模糊查询，回车跳过）：");
        String name = scanner.nextLine().trim();
        System.out.print("学号（模糊查询，回车跳过）：");
        String studentNo = scanner.nextLine().trim();
        System.out.print("专业（模糊查询，回车跳过）：");
        String major = scanner.nextLine().trim();
        
        PageResult<Student> page = studentService.searchStudents(1, 10, 
                name.isEmpty() ? null : name,
                studentNo.isEmpty() ? null : studentNo,
                major.isEmpty() ? null : major);
        
        System.out.println("\n检索结果：" + page.getTotal() + " 条");
        System.out.println("----------------------------------------");
        page.getRecords().forEach(s -> {
            System.out.println("ID:" + s.getId() + " | " + s.getName() + " | " + s.getStudentNo() + " | " + s.getMajor());
        });
    }
    
    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("    学生管理");
        System.out.println("========================================");
        System.out.println("1. 新增学生");
        System.out.println("2. 修改学生信息");
        System.out.println("3. 删除学生");
        System.out.println("4. 查询学生详情（含级联信息）");
        System.out.println("5. 学生列表（分页）");
        System.out.println("6. 条件检索");
        System.out.println("0. 返回主菜单");
        System.out.println("========================================");
        System.out.print("请选择：");
    }
}
