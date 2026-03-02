package com.yuxuanlei.service;

import com.yuxuanlei.entity.Book;
import com.yuxuanlei.util.PageResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 图书服务测试类
 */
class BookServiceTest {
    
    private BookService bookService;
    
    @BeforeEach
    void setUp() {
        bookService = new BookService();
    }
    
    @Test
    void testAddBook() {
        Book book = new Book();
        book.setBookName("测试图书");
        book.setAuthor("测试作者");
        book.setIsbn("TEST-ISBN-" + System.currentTimeMillis());
        book.setTotalCount(10);
        
        Long id = bookService.addBook(book);
        assertNotNull(id);
        
        Book saved = bookService.getBookById(id);
        assertEquals(10, saved.getAvailableCount());
        
        // 清理
        bookService.deleteBook(id);
    }
    
    @Test
    void testSearchBooks() {
        PageResult<Book> page = bookService.searchBooks(1, 10, "Java", null, null);
        assertNotNull(page);
    }
    
    @Test
    void testDecreaseAvailableCount() {
        Book book = new Book();
        book.setBookName("库存测试");
        book.setAuthor("作者");
        book.setIsbn("STOCK-" + System.currentTimeMillis());
        book.setTotalCount(5);
        Long id = bookService.addBook(book);
        
        bookService.decreaseAvailableCount(id);
        
        Book updated = bookService.getBookById(id);
        assertEquals(4, updated.getAvailableCount());
        
        // 清理
        bookService.deleteBook(id);
    }
    
    @Test
    void testIncreaseAvailableCount() {
        Book book = new Book();
        book.setBookName("库存测试2");
        book.setAuthor("作者");
        book.setIsbn("STOCK2-" + System.currentTimeMillis());
        book.setTotalCount(5);
        book.setAvailableCount(3);
        Long id = bookService.addBook(book);
        
        bookService.increaseAvailableCount(id);
        
        Book updated = bookService.getBookById(id);
        assertEquals(4, updated.getAvailableCount());
        
        // 清理
        bookService.deleteBook(id);
    }
}
