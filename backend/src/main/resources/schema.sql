-- 设置客户端连接字符集，防止中文乱码
SET NAMES utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_db;

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    age INT COMMENT '年龄',
    major VARCHAR(100) COMMENT '专业',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 借书证表（1:1 关联学生）
CREATE TABLE IF NOT EXISTS library_card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL UNIQUE COMMENT '学生ID',
    card_no VARCHAR(30) NOT NULL UNIQUE COMMENT '借书证号',
    issue_date DATE NOT NULL COMMENT '发证日期',
    expire_date DATE NOT NULL COMMENT '过期日期',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-有效，EXPIRED-过期，SUSPENDED-挂失',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借书证表';

-- 图书表
CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    book_name VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) COMMENT '作者',
    isbn VARCHAR(20) UNIQUE COMMENT 'ISBN',
    total_count INT DEFAULT 0 COMMENT '总数量',
    available_count INT DEFAULT 0 COMMENT '可借数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 借阅记录表（1:N 关联学生和图书）
CREATE TABLE IF NOT EXISTS book_borrow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_date DATETIME NOT NULL COMMENT '借书日期',
    return_date DATETIME COMMENT '还书日期',
    status VARCHAR(20) DEFAULT 'BORROWED' COMMENT '状态：BORROWED-已借出，RETURNED-已归还',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20) NOT NULL UNIQUE COMMENT '课程代码',
    credits INT DEFAULT 0 COMMENT '学分',
    teacher VARCHAR(50) COMMENT '授课教师',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 学生选课表（M:N 关联学生和课程）
CREATE TABLE IF NOT EXISTS student_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    enroll_date DATE NOT NULL COMMENT '选课日期',
    score DECIMAL(5,2) COMMENT '成绩',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY uk_student_course (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生选课表';

-- ========== 插入测试数据（支持重复执行，遇重复则跳过） ==========

-- 俞轩磊学生记录
INSERT INTO student (name, student_no, age, major) VALUES ('俞轩磊', '2024001', 20, '计算机科学与技术')
ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);
SET @yu_student_id = LAST_INSERT_ID();

-- 为俞轩磊办理借书证
INSERT IGNORE INTO library_card (student_id, card_no, issue_date, expire_date, status)
VALUES (@yu_student_id, 'LC2024001', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 4 YEAR), 'ACTIVE');

-- 图书数据（按 isbn 唯一约束防重复；第一本初始 5 可借，借出后变为 4）
INSERT IGNORE INTO book (book_name, author, isbn, total_count, available_count) VALUES
('Java 核心技术', 'Cay S. Horstmann', '9787111544968', 5, 5),
('Spring Boot 实战', '汪云飞', '9787121302084', 3, 3),
('MySQL 必知必会', 'Ben Forta', '9787115404596', 4, 4);
SET @book_first_id = (SELECT id FROM book WHERE isbn = '9787111544968' AND deleted = 0 LIMIT 1);

-- 俞轩磊借书记录（仅当不存在未归还的同书借阅时插入）
INSERT INTO book_borrow (student_id, book_id, borrow_date, status)
SELECT @yu_student_id, @book_first_id, NOW(), 'BORROWED'
FROM DUAL
WHERE @yu_student_id IS NOT NULL AND @yu_student_id > 0
  AND @book_first_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM book_borrow
    WHERE student_id = @yu_student_id AND book_id = @book_first_id
      AND status = 'BORROWED' AND deleted = 0
  );

-- 更新图书可借数量（仅当尚未扣减过时执行：首次 available_count=5，扣减后=4）
UPDATE book SET available_count = available_count - 1
WHERE id = @book_first_id AND available_count = 5;

-- 课程数据（按 course_code 唯一约束防重复）
INSERT IGNORE INTO course (course_name, course_code, credits, teacher) VALUES
('数据结构', 'CS101', 4, '张教授'),
('数据库原理', 'CS102', 3, '李教授'),
('操作系统', 'CS103', 4, '王教授');

-- 俞轩磊选课记录（按 uk_student_course 唯一约束，INSERT IGNORE 防重复）
INSERT IGNORE INTO student_course (student_id, course_id, enroll_date, score)
SELECT @yu_student_id, id, CURDATE(), 95.5 FROM course WHERE course_code = 'CS101' AND deleted = 0
UNION ALL
SELECT @yu_student_id, id, CURDATE(), 88.0 FROM course WHERE course_code = 'CS102' AND deleted = 0;
