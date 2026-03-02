# API 文档

## 概述

本文档详细说明了图书管理系统的 **对外 Service 层 API 接口**，供其他模块或上层 CLI 调用。

### 接口分类

| 服务 | 职责 | 主要方法 |
|------|------|----------|
| StudentService | 学生 CRUD、级联查询 | addStudent, updateStudent, getStudentWithDetails, searchStudents |
| LibraryCardService | 借书证办理、续期、状态 | issueCard, renewCard, getCardByStudentId |
| BookService | 图书 CRUD、库存 | addBook, borrowBook（通过 BookBorrowService 调用） |
| BookBorrowService | 借还书 | borrowBook, returnBook, getBorrowsByStudentId |
| CourseService | 课程 CRUD、选课记录 | addCourse, getCourseEnrollmentsByStudentId |
| StudentCourseService | 选课、退课、成绩 | enrollCourse, dropCourse, updateScore |

### 通用异常

- `IllegalArgumentException`：参数校验失败
- `DuplicateDataException`：数据重复（如学号、课程代码已存在）
- `DataNotFoundException`：数据不存在
- `BusinessException`：其他业务异常，`getMessage()` 返回可读说明

## 1. StudentService（学生服务）

### 1.1 新增学生

```java
public Long addStudent(Student student)
```

**功能：** 新增学生记录

**参数：**
- `student`: 学生对象
  - `name` (String, 必填): 学生姓名，长度1-50
  - `studentNo` (String, 必填): 学号，长度1-20，唯一
  - `age` (Integer, 可选): 年龄，范围1-150
  - `major` (String, 可选): 专业，长度0-100

**返回值：** 新增学生的 ID

**异常：**
- `IllegalArgumentException`: 参数校验失败
- `DuplicateDataException`: 学号已存在
- `BusinessException`: 其他业务异常

**示例：**
```java
Student student = new Student();
student.setName("张三");
student.setStudentNo("2024001");
student.setAge(20);
student.setMajor("计算机科学");

Long id = studentService.addStudent(student);
```

### 1.2 更新学生信息

```java
public boolean updateStudent(Student student)
```

**功能：** 更新学生信息

**参数：**
- `student`: 学生对象（必须包含 ID）

**返回值：** 更新成功返回 true，失败返回 false

**异常：**
- `IllegalArgumentException`: 参数校验失败
- `DataNotFoundException`: 学生不存在
- `BusinessException`: 其他业务异常

### 1.3 删除学生

```java
public boolean deleteStudent(Long id)
```

**功能：** 删除学生（逻辑删除，级联删除借书证、借阅记录、选课记录）

**参数：**
- `id` (Long, 必填): 学生 ID

**返回值：** 删除成功返回 true

**异常：**
- `IllegalArgumentException`: ID 无效
- `DataNotFoundException`: 学生不存在
- `BusinessException`: 其他业务异常

### 1.4 查询学生

```java
public Student getStudentById(Long id)
```

**功能：** 根据 ID 查询学生基本信息

**参数：**
- `id` (Long, 必填): 学生 ID

**返回值：** 学生对象，不存在返回 null

### 1.5 查询学生详情（级联查询）

```java
public Student getStudentWithDetails(Long id)
```

**功能：** 查询学生完整信息，包含：
- 借书证信息（1:1 级联）
- 借阅记录列表（1:N，`bookBorrows`）
- 所借图书列表（1:N，`borrowedBooks`，通过 book_borrow 中间表关联，详见 project_design.md 2.1）
- 选课记录列表（M:N，`courseEnrollments`，每条含 `course` 关联及中间表 `score`）

**参数：**
- `id` (Long, 必填): 学生 ID

**返回值：** 包含级联信息的学生对象

**异常：**
- `DataNotFoundException`: 学生不存在

### 1.6 分页查询学生列表

```java
public PageResult<Student> listStudents(int pageNum, int pageSize)
```

**功能：** 分页查询所有学生

**参数：**
- `pageNum` (int, 必填): 页码，从 1 开始
- `pageSize` (int, 必填): 每页大小，范围 1-100

**返回值：** 分页结果对象
- `records`: 学生列表
- `total`: 总记录数
- `current`: 当前页码
- `pages`: 总页数

**异常：**
- `IllegalArgumentException`: 分页参数无效

### 1.7 条件检索学生

```java
public PageResult<Student> searchStudents(int pageNum, int pageSize, 
                                          String name, String studentNo, String major)
```

**功能：** 根据条件检索学生（模糊查询）

**参数：**
- `pageNum` (int, 必填): 页码
- `pageSize` (int, 必填): 每页大小
- `name` (String, 可选): 姓名（模糊匹配）
- `studentNo` (String, 可选): 学号（模糊匹配）
- `major` (String, 可选): 专业（模糊匹配）

**返回值：** 分页结果对象

### 1.8 根据学号查询学生

```java
public Student getStudentByNo(String studentNo)
```

**功能：** 根据学号查询学生

**参数：**
- `studentNo` (String, 必填): 学号

**返回值：** 学生对象，不存在返回 null

---

## 2. LibraryCardService（借书证服务）

### 2.1 办理借书证

```java
public Long issueCard(LibraryCard card)
```

**功能：** 为学生办理借书证（1:1 关系）

**参数：**
- `card`: 借书证对象
  - `studentId` (Long, 必填): 学生 ID
  - `cardNo` (String, 必填): 借书证号，长度1-30
  - `issueDate` (LocalDate, 可选): 发证日期，默认当前日期
  - `expireDate` (LocalDate, 可选): 过期日期，默认4年后
  - `status` (String, 可选): 状态，默认 ACTIVE

**返回值：** 借书证 ID

**异常：**
- `IllegalArgumentException`: 参数校验失败
- `DuplicateDataException`: 该学生已有借书证
- `BusinessException`: 其他业务异常

**状态枚举：**
- `ACTIVE`: 有效
- `EXPIRED`: 过期
- `SUSPENDED`: 挂失

### 2.2 续期借书证

```java
public boolean renewCard(Long id, int years)
```

**功能：** 续期借书证

**参数：**
- `id` (Long, 必填): 借书证 ID
- `years` (int, 必填): 续期年数

**返回值：** 续期成功返回 true

### 2.3 更新借书证状态

```java
public boolean updateCardStatus(Long id, String status)
```

**功能：** 更新借书证状态

**参数：**
- `id` (Long, 必填): 借书证 ID
- `status` (String, 必填): 新状态（ACTIVE/EXPIRED/SUSPENDED）

**返回值：** 更新成功返回 true

### 2.4 查询借书证

```java
public LibraryCard getCardByStudentId(Long studentId)
public LibraryCard getCardById(Long id)
public LibraryCard getCardByIdWithStudent(Long id)      // 含关联学生（双向 1:1）
public LibraryCard getCardByStudentIdWithStudent(Long studentId)  // 含关联学生
```

**功能：** 查询借书证

**返回值：** 借书证对象，不存在返回 null

### 2.5 分页查询借书证列表

```java
public PageResult<LibraryCard> listCards(int pageNum, int pageSize)
```

**功能：** 分页查询所有借书证

**返回值：** 分页结果对象

---

## 3. BookService（图书服务）

### 3.1 新增图书

```java
public Long addBook(Book book)
```

**功能：** 新增图书

**参数：**
- `book`: 图书对象
  - `bookName` (String, 必填): 书名，长度1-200
  - `author` (String, 可选): 作者，长度0-100
  - `isbn` (String, 可选): ISBN，长度0-20
  - `totalCount` (Integer, 必填): 总数量，≥0
  - `availableCount` (Integer, 可选): 可借数量，默认等于总数量

**返回值：** 图书 ID

**校验规则：**
- 可借数量 ≤ 总数量
- 数量不能为负数

### 3.2 更新图书信息

```java
public boolean updateBook(Book book)
```

### 3.3 删除图书

```java
public boolean deleteBook(Long id)
```

### 3.4 查询图书

```java
public Book getBookById(Long id)
```

### 3.5 分页查询图书列表

```java
public PageResult<Book> listBooks(int pageNum, int pageSize)
```

### 3.6 条件检索图书

```java
public PageResult<Book> searchBooks(int pageNum, int pageSize,
                                    String bookName, String author, String isbn)
```

**功能：** 根据条件检索图书（模糊查询）

**参数：**
- `bookName` (String, 可选): 书名
- `author` (String, 可选): 作者
- `isbn` (String, 可选): ISBN

### 3.7 库存管理

```java
public boolean decreaseAvailableCount(Long bookId)
public boolean increaseAvailableCount(Long bookId)
```

**功能：** 减少/增加图书可借数量

**使用场景：**
- 借书时调用 `decreaseAvailableCount`
- 还书时调用 `increaseAvailableCount`

---

## 4. BookBorrowService（借阅服务）

### 4.1 借书

```java
public Long borrowBook(BookBorrow borrow)
```

**功能：** 借书（1:N 关系）

**参数：**
- `borrow`: 借阅记录对象
  - `studentId` (Long, 必填): 学生 ID
  - `bookId` (Long, 必填): 图书 ID
  - `borrowDate` (LocalDateTime, 可选): 借书日期，默认当前时间
  - `status` (String, 可选): 状态，默认 BORROWED

**返回值：** 借阅记录 ID

**业务逻辑：**
1. 检查图书库存
2. 减少图书可借数量
3. 创建借阅记录

**异常：**
- `BusinessException`: 图书库存不足

### 4.2 还书

```java
public boolean returnBook(Long borrowId)
```

**功能：** 还书

**参数：**
- `borrowId` (Long, 必填): 借阅记录 ID

**业务逻辑：**
1. 检查借阅记录状态
2. 增加图书可借数量
3. 更新借阅记录状态为 RETURNED

### 4.3 查询借阅记录

```java
public List<BookBorrow> getBorrowsByStudentId(Long studentId)
public BookBorrow getBorrowWithDetails(Long id)
public PageResult<BookBorrow> listBorrows(int pageNum, int pageSize)
```

**功能：** 查询借阅记录

**返回值：**
- `getBorrowsByStudentId`: 学生的所有借阅记录（含图书信息）
- `getBorrowWithDetails`: 借阅记录详情（含学生和图书信息）
- `listBorrows`: 分页查询所有借阅记录

---

## 5. CourseService（课程服务）

### 5.1 新增课程

```java
public Long addCourse(Course course)
```

**参数：**
- `course`: 课程对象
  - `courseName` (String, 必填): 课程名称，长度1-100
  - `courseCode` (String, 必填): 课程代码，长度1-20，唯一
  - `credits` (Integer, 可选): 学分，范围0-10
  - `teacher` (String, 可选): 教师，长度0-50

### 5.2 更新课程信息

```java
public boolean updateCourse(Course course)
```

### 5.3 删除课程

```java
public boolean deleteCourse(Long id)
```

### 5.4 查询课程

```java
public Course getCourseById(Long id)
public Course getCourseByCourseCode(String courseCode)
```

### 5.5 分页查询课程列表

```java
public PageResult<Course> listCourses(int pageNum, int pageSize)
```

### 5.6 查询课程的选修学生

```java
public Course getCourseWithStudents(Long courseId)
```

**功能：** 查询课程及其选修学生列表（M:N 关系，含 students 级联）

### 5.7 查询学生选修的课程

```java
public List<StudentCourse> getCourseEnrollmentsByStudentId(Long studentId)
```

**功能：** 查询学生选课记录（含课程及成绩）。返回 `List<StudentCourse>`，每条记录含 `course` 关联及中间表 `score`。

---

## 6. StudentCourseService（选课服务）

### 6.1 学生选课

```java
public Long enrollCourse(Long studentId, Long courseId)
```

**功能：** 学生选课（M:N 关系）

**参数：**
- `studentId` (Long, 必填): 学生 ID
- `courseId` (Long, 必填): 课程 ID

**返回值：** 选课记录 ID

**异常：**
- `DuplicateDataException`: 已选修该课程

### 6.2 学生退课

```java
public boolean dropCourse(Long studentId, Long courseId)
```

**功能：** 学生退课

**返回值：** 退课成功返回 true

### 6.3 更新成绩

```java
public boolean updateScore(Long studentId, Long courseId, BigDecimal score)
```

**功能：** 更新学生课程成绩

**参数：**
- `score` (BigDecimal, 必填): 成绩，范围0-100

### 6.4 查询选课记录

```java
public StudentCourse getStudentCourse(Long studentId, Long courseId)
```

**功能：** 查询学生的选课记录

**返回值：** 选课记录对象，不存在返回 null

---

## 通用说明

### 异常处理

所有 Service 方法可能抛出以下异常：

1. **IllegalArgumentException**: 参数校验失败
   - 参数为 null
   - 参数格式不正确
   - 参数超出范围

2. **BusinessException**: 业务异常
   - 包含错误码和错误信息
   - 可通过 `getErrorCode()` 获取错误码

3. **DuplicateDataException**: 数据重复异常
   - 继承自 BusinessException
   - 错误码：DUPLICATE_DATA

4. **DataNotFoundException**: 数据不存在异常
   - 继承自 BusinessException
   - 错误码：DATA_NOT_FOUND

### 事务管理

所有写操作（新增、更新、删除）都使用事务：
- 操作成功自动提交
- 操作失败自动回滚
- 资源自动释放

### 日志记录

所有关键操作都会记录日志：
- INFO 级别：正常业务操作
- ERROR 级别：异常情况

### 分页查询

分页参数统一规则：
- `pageNum`: 页码，从 1 开始
- `pageSize`: 每页大小，范围 1-100
- 返回 `PageResult` 对象，包含：
  - `records`: 当前页数据
  - `total`: 总记录数
  - `current`: 当前页码
  - `pages`: 总页数
  - `size`: 每页大小

### 级联关系

系统支持三种级联关系：

1. **1:1 关系**：学生 ←→ 借书证
   - 一个学生只能有一张借书证
   - 删除学生时级联删除借书证

2. **学生-借阅记录 1:N**，**学生-所借图书 1:N**（Prompt 要求）
   - 学生 (1) ←→ (N) 借阅记录；学生 (1) ←→ (N) 所借图书（`borrowedBooks`，经 book_borrow 中间表）
   - 删除学生时级联删除借阅记录

3. **M:N 关系**：学生 ←→ 课程
   - 一个学生可以选修多门课程
   - 一门课程可以被多个学生选修
   - 通过 `student_course` 中间表实现
   - 删除学生时级联删除选课记录
