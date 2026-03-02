package com.yuxuanlei.cli;

import com.yuxuanlei.entity.Book;
import com.yuxuanlei.service.BookService;
import com.yuxuanlei.util.PageResult;

import java.util.Scanner;

/**
 * 图书管理菜单
 */
public class BookMenu {
    
    private final BookService bookService;
    private final Scanner scanner = new Scanner(System.in);
    
    public BookMenu(BookService bookService) {
        this.bookService = bookService;
    }
    
    public void show() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        addBook();
                        break;
                    case "2":
                        updateBook();
                        break;
                    case "3":
                        deleteBook();
                        break;
                    case "4":
                        listBooks();
                        break;
                    case "5":
                        searchBooks();
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
    
    private void addBook() {
        System.out.println("\n=== 新增图书 ===");
        System.out.print("书名：");
        String bookName = scanner.nextLine().trim();
        System.out.print("作者：");
        String author = scanner.nextLine().trim();
        System.out.print("ISBN：");
        String isbn = scanner.nextLine().trim();
        System.out.print("总数量：");
        int totalCount = Integer.parseInt(scanner.nextLine().trim());
        
        Book book = new Book();
        book.setBookName(bookName);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setTotalCount(totalCount);
        
        Long id = bookService.addBook(book);
        System.out.println("✓ 新增成功！图书ID：" + id);
    }
    
    private void updateBook() {
        System.out.println("\n=== 修改图书信息 ===");
        System.out.print("图书ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        Book book = bookService.getBookById(id);
        if (book == null) {
            System.out.println("✗ 图书不存在！");
            return;
        }
        
        System.out.println("当前信息：" + book.getBookName() + " | " + book.getAuthor());
        System.out.print("新书名（回车跳过）：");
        String bookName = scanner.nextLine().trim();
        if (!bookName.isEmpty()) {
            book.setBookName(bookName);
        }
        
        System.out.print("新作者（回车跳过）：");
        String author = scanner.nextLine().trim();
        if (!author.isEmpty()) {
            book.setAuthor(author);
        }
        
        bookService.updateBook(book);
        System.out.println("✓ 更新成功！");
    }
    
    private void deleteBook() {
        System.out.println("\n=== 删除图书 ===");
        System.out.print("图书ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        
        System.out.print("确认删除？(y/n)：");
        String confirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消");
            return;
        }
        
        bookService.deleteBook(id);
        System.out.println("✓ 删除成功！");
    }
    
    private void listBooks() {
        System.out.println("\n=== 图书列表（分页） ===");
        System.out.print("页码（默认1）：");
        String pageStr = scanner.nextLine().trim();
        int pageNum = pageStr.isEmpty() ? 1 : Integer.parseInt(pageStr);
        
        PageResult<Book> page = bookService.listBooks(pageNum, 10);
        
        System.out.println("\n总记录数：" + page.getTotal() + " | 当前页：" + page.getCurrent() + "/" + page.getPages());
        System.out.println("----------------------------------------");
        page.getRecords().forEach(book -> {
            System.out.println("ID:" + book.getId() + " | " + book.getBookName() 
                    + " | " + book.getAuthor() + " | 可借:" + book.getAvailableCount());
        });
    }
    
    private void searchBooks() {
        System.out.println("\n=== 条件检索 ===");
        System.out.print("书名（模糊查询，回车跳过）：");
        String bookName = scanner.nextLine().trim();
        System.out.print("作者（模糊查询，回车跳过）：");
        String author = scanner.nextLine().trim();
        System.out.print("ISBN（模糊查询，回车跳过）：");
        String isbn = scanner.nextLine().trim();
        
        PageResult<Book> page = bookService.searchBooks(1, 10,
                bookName.isEmpty() ? null : bookName,
                author.isEmpty() ? null : author,
                isbn.isEmpty() ? null : isbn);
        
        System.out.println("\n检索结果：" + page.getTotal() + " 条");
        System.out.println("----------------------------------------");
        page.getRecords().forEach(book -> {
            System.out.println("ID:" + book.getId() + " | " + book.getBookName() 
                    + " | " + book.getAuthor() + " | 可借:" + book.getAvailableCount());
        });
    }
    
    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("    图书管理");
        System.out.println("========================================");
        System.out.println("1. 新增图书");
        System.out.println("2. 修改图书信息");
        System.out.println("3. 删除图书");
        System.out.println("4. 图书列表（分页）");
        System.out.println("5. 条件检索");
        System.out.println("0. 返回主菜单");
        System.out.println("========================================");
        System.out.print("请选择：");
    }
}
