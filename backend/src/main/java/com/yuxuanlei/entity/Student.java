package com.yuxuanlei.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 学生实体类
 */
public class Student {
    
    private Long id;
    private String name;
    private String studentNo;
    private Integer age;
    private String major;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
    
    // 一对一：借书证
    private LibraryCard libraryCard;
    
    // 一对多：借阅记录（学生 1:N 借阅记录）
    private List<BookBorrow> bookBorrows;
    
    // 一对多：所借图书（从学生视角：1 学生 : N 本书；通过 book_borrow 中间表实现，
    // 因业务需记录借阅历史、归还日期等，无法用 book.student_id 直连，故采用中间表）
    private List<Book> borrowedBooks;
    
    // 多对多：选课记录（通过 StudentCourse 中间表，score 属中间表字段，放在关联类中）
    private List<StudentCourse> courseEnrollments;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public LibraryCard getLibraryCard() {
        return libraryCard;
    }

    public void setLibraryCard(LibraryCard libraryCard) {
        this.libraryCard = libraryCard;
    }

    public List<BookBorrow> getBookBorrows() {
        return bookBorrows;
    }

    public void setBookBorrows(List<BookBorrow> bookBorrows) {
        this.bookBorrows = bookBorrows;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<Book> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public List<StudentCourse> getCourseEnrollments() {
        return courseEnrollments;
    }

    public void setCourseEnrollments(List<StudentCourse> courseEnrollments) {
        this.courseEnrollments = courseEnrollments;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', studentNo='" + studentNo + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
