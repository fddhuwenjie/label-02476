package com.yuxuanlei.util;

import com.yuxuanlei.entity.*;

/**
 * 输入校验工具类
 */
public class ValidationUtil {
    
    /**
     * 校验学生信息
     */
    public static void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("学生信息不能为空");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("学生姓名不能为空");
        }
        if (student.getName().length() > 50) {
            throw new IllegalArgumentException("学生姓名长度不能超过50个字符");
        }
        if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        if (student.getStudentNo().length() > 20) {
            throw new IllegalArgumentException("学号长度不能超过20个字符");
        }
        if (student.getAge() != null && (student.getAge() < 1 || student.getAge() > 150)) {
            throw new IllegalArgumentException("年龄必须在1-150之间");
        }
        if (student.getMajor() != null && student.getMajor().length() > 100) {
            throw new IllegalArgumentException("专业名称长度不能超过100个字符");
        }
    }
    
    /**
     * 校验图书信息
     */
    public static void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("图书信息不能为空");
        }
        if (book.getBookName() == null || book.getBookName().trim().isEmpty()) {
            throw new IllegalArgumentException("图书名称不能为空");
        }
        if (book.getBookName().length() > 200) {
            throw new IllegalArgumentException("图书名称长度不能超过200个字符");
        }
        if (book.getAuthor() != null && book.getAuthor().length() > 100) {
            throw new IllegalArgumentException("作者名称长度不能超过100个字符");
        }
        if (book.getIsbn() != null && book.getIsbn().length() > 20) {
            throw new IllegalArgumentException("ISBN长度不能超过20个字符");
        }
        if (book.getTotalCount() != null && book.getTotalCount() < 0) {
            throw new IllegalArgumentException("图书总数量不能为负数");
        }
        if (book.getAvailableCount() != null && book.getAvailableCount() < 0) {
            throw new IllegalArgumentException("图书可借数量不能为负数");
        }
        if (book.getTotalCount() != null && book.getAvailableCount() != null 
                && book.getAvailableCount() > book.getTotalCount()) {
            throw new IllegalArgumentException("可借数量不能大于总数量");
        }
    }
    
    /**
     * 校验借书证信息
     */
    public static void validateLibraryCard(LibraryCard card) {
        if (card == null) {
            throw new IllegalArgumentException("借书证信息不能为空");
        }
        if (card.getStudentId() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        if (card.getCardNo() == null || card.getCardNo().trim().isEmpty()) {
            throw new IllegalArgumentException("借书证号不能为空");
        }
        if (card.getCardNo().length() > 30) {
            throw new IllegalArgumentException("借书证号长度不能超过30个字符");
        }
        // status 为枚举类型，非空即合法
        if (card.getIssueDate() != null && card.getExpireDate() != null 
                && card.getExpireDate().isBefore(card.getIssueDate())) {
            throw new IllegalArgumentException("过期日期不能早于发证日期");
        }
    }
    
    /**
     * 校验借阅记录
     */
    public static void validateBookBorrow(BookBorrow borrow) {
        if (borrow == null) {
            throw new IllegalArgumentException("借阅记录不能为空");
        }
        if (borrow.getStudentId() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        if (borrow.getBookId() == null) {
            throw new IllegalArgumentException("图书ID不能为空");
        }
        // status 为枚举类型，非空即合法
    }
    
    /**
     * 校验课程信息
     */
    public static void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("课程信息不能为空");
        }
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        if (course.getCourseName().length() > 100) {
            throw new IllegalArgumentException("课程名称长度不能超过100个字符");
        }
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            throw new IllegalArgumentException("课程代码不能为空");
        }
        if (course.getCourseCode().length() > 20) {
            throw new IllegalArgumentException("课程代码长度不能超过20个字符");
        }
        if (course.getCredits() != null && (course.getCredits() < 0 || course.getCredits() > 10)) {
            throw new IllegalArgumentException("学分必须在0-10之间");
        }
        if (course.getTeacher() != null && course.getTeacher().length() > 50) {
            throw new IllegalArgumentException("教师名称长度不能超过50个字符");
        }
    }
    
    /**
     * 校验选课信息
     */
    public static void validateStudentCourse(StudentCourse sc) {
        if (sc == null) {
            throw new IllegalArgumentException("选课信息不能为空");
        }
        if (sc.getStudentId() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        if (sc.getCourseId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (sc.getScore() != null && 
                (sc.getScore().doubleValue() < 0 || sc.getScore().doubleValue() > 100)) {
            throw new IllegalArgumentException("成绩必须在0-100之间");
        }
    }
    
    /**
     * 校验分页参数
     */
    public static void validatePageParams(int pageNum, int pageSize) {
        if (pageNum < 1) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("每页大小必须在1-100之间");
        }
    }
    
    /**
     * 校验ID参数
     */
    public static void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldName + "必须是正整数");
        }
    }
    
    /**
     * 校验成绩
     */
    public static void validateScore(java.math.BigDecimal score) {
        if (score == null) {
            throw new IllegalArgumentException("成绩不能为空");
        }
        if (score.compareTo(java.math.BigDecimal.ZERO) < 0 || score.compareTo(new java.math.BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("成绩必须在0-100之间");
        }
    }
}
