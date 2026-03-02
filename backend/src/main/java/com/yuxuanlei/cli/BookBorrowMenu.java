package com.yuxuanlei.cli;

import com.yuxuanlei.entity.BookBorrow;
import com.yuxuanlei.service.BookBorrowService;
import com.yuxuanlei.util.PageResult;

import java.util.List;
import java.util.Scanner;

/**
 * 借阅管理菜单
 */
public class BookBorrowMenu {
    
    private final BookBorrowService bookBorrowService;
    private final Scanner scanner = new Scanner(System.in);
    
    public BookBorrowMenu(BookBorrowService bookBorrowService) {
        this.bookBorrowService = bookBorrowService;
    }
    
    public void show() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        borrowBook();
                        break;
                    case "2":
                        returnBook();
                        break;
                    case "3":
                        viewStudentBorrows();
                        break;
                    case "4":
                        listBorrows();
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
    
    private void borrowBook() {
        System.out.println("\n=== 借书（1:N 级联） ===");
        System.out.print("学生ID：");
        Long studentId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("图书ID：");
        Long bookId = Long.parseLong(scanner.nextLine().trim());
        
        BookBorrow borrow = new BookBorrow();
        borrow.setStudentId(studentId);
        borrow.setBookId(bookId);
        
        Long id = bookBorrowService.borrowBook(borrow);
        System.out.println("✓ 借书成功！借阅记录ID：" + id);
    }
    
    private void returnBook() {
        System.out.println("\n=== 还书 ===");
        System.out.print("借阅记录ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        bookBorrowService.returnBook(id);
        System.out.println("✓ 还书成功！");
    }
    
    private void viewStudentBorrows() {
        System.out.println("\n=== 查询学生借阅记录 ===");
        System.out.print("学生ID：");
        Long studentId = Long.parseLong(scanner.nextLine().trim());
        
        List<BookBorrow> borrows = bookBorrowService.getBorrowsByStudentId(studentId);
        
        System.out.println("\n借阅记录：" + borrows.size() + " 条");
        System.out.println("----------------------------------------");
        borrows.forEach(borrow -> {
            System.out.println("ID:" + borrow.getId() 
                    + " | 图书:" + (borrow.getBook() != null ? borrow.getBook().getBookName() : "未知")
                    + " | 状态:" + borrow.getStatus()
                    + " | 借书日期:" + borrow.getBorrowDate());
        });
    }
    
    private void listBorrows() {
        System.out.println("\n=== 借阅记录列表（分页） ===");
        System.out.print("页码（默认1）：");
        String pageStr = scanner.nextLine().trim();
        int pageNum = pageStr.isEmpty() ? 1 : Integer.parseInt(pageStr);
        
        PageResult<BookBorrow> page = bookBorrowService.listBorrows(pageNum, 10);
        
        System.out.println("\n总记录数：" + page.getTotal() + " | 当前页：" + page.getCurrent() + "/" + page.getPages());
        System.out.println("----------------------------------------");
        page.getRecords().forEach(borrow -> {
            System.out.println("ID:" + borrow.getId() 
                    + " | 学生ID:" + borrow.getStudentId()
                    + " | 图书ID:" + borrow.getBookId()
                    + " | 状态:" + borrow.getStatus());
        });
    }
    
    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("    借阅管理");
        System.out.println("========================================");
        System.out.println("1. 借书（1:N 级联）");
        System.out.println("2. 还书");
        System.out.println("3. 查询学生借阅记录");
        System.out.println("4. 借阅记录列表（分页）");
        System.out.println("0. 返回主菜单");
        System.out.println("========================================");
        System.out.print("请选择：");
    }
}
