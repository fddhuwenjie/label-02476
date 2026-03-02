# 图书管理系统

基于纯 MyBatis + Java SE 的图书管理系统，实现学生、借书证、图书、借阅、课程的完整管理功能，包含 1:1、1:N、M:N 三种级联关系。

## How to Run

### IntelliJ IDEA 导入（推荐开发方式）

本项目为 Maven 项目，**必须导入 `backend/` 目录**（内含 `pom.xml`），不要导入项目根目录。

1. **File → Open**，选择克隆后的项目中的 **`backend`** 文件夹
2. 若提示 "Load Maven Project" 或 "Import Maven Project"，点击确认
3. 等待依赖下载完成，运行 `LibraryManagementApplication.main()` 即可启动
4. 项目已含 `.idea/` 与 `library-management.iml`，IDEA 会识别为标准 Maven 项目

> 本地开发时需先启动 MySQL 并执行 `schema.sql`；可设置环境变量 `DB_HOST=localhost` 连接本地数据库。

### 前置要求
- Docker
- Docker Compose

### 启动步骤

1. 克隆项目到本地
```bash
git clone <repository-url>
cd <克隆后的项目目录>   # 目录名由远程仓库名决定，如 library-management-system
```

2. 一键启动（推荐）
```bash
cd backend

# Linux / Mac
chmod +x start.sh
./start.sh

# Windows（任选其一）
.\start.bat        # 双击或命令行运行，推荐
.\start.ps1        # PowerShell 运行
```
脚本会自动构建、启动 Docker 并进入命令行界面。

3. 手动分步启动（可选）
```bash
# 在项目根目录执行
docker-compose up --build -d
docker logs -f library-backend   # 查看日志
docker exec -it library-backend java -jar /app/app.jar   # 进入交互界面
```

### 停止服务
```bash
docker-compose down
```

### 清理数据
```bash
docker-compose down -v
```

### 纯 Java 运行（无 Docker）

需已安装 JDK 17+、Maven、MySQL，且数据库已初始化（执行 `schema.sql`）。本地开发时请设置 `DB_HOST=localhost`。

```bash
cd backend
mvn clean package
java -jar target/library-management-fat.jar
```

### 文件打包提交

按要求将项目压缩打包提交时，可使用以下脚本：

**Linux / Mac：**
```bash
cd backend
chmod +x pack.sh
./pack.sh
```

**Windows PowerShell：**
```powershell
cd backend
.\pack.ps1
```

脚本会生成 `library-management-system_yyyyMMdd_HHmmss.zip`（位于项目根目录），已排除 `target/`、`.git/` 等无关目录。将生成的 zip 文件提交至指定平台即可。

## Services

### MySQL 数据库
- 端口：3306
- 数据库名：library_db
- 用户名：root
- 密码：root123456

### 后端服务
- 容器名：library-backend
- 技术栈：Java 17 + MyBatis 3.5.13 + MySQL 8.0
- 交互方式：命令行菜单（CLI）
- 日志框架：SLF4J + Logback

## 测试账号

系统已预置测试数据：

| 学生姓名 | 学号 | 年龄 | 专业 |
|---------|------|------|------|
| 俞轩磊 | 2024001 | 20 | 计算机科学与技术 |

俞轩磊的完整信息：
- 借书证号：LC2024001
- 已借图书：《Java 核心技术》
- 选修课程：数据结构（95.5分）、数据库原理（88.0分）

## 题目内容

请编写Mybatis的IDEA项目，并将文件压缩打包提交，项目要求： 
1）建立学生和借书证之间的一对一级联； 
2）建立学生和所借图书之间的一对多级联； 
3）建立学生和选修课之间的多对多接连； 
4）顶层软件包命名规则： com.yuxuanlei ； 
5）所有和学生有关的表中用俞轩磊 新建一条记录

### 项目要求

1. 建立学生和借书证之间的 **1:1 级联**
2. 建立学生和所借图书之间的 **1:N 级联**
3. 建立学生和选修课之间的 **M:N 级联**
4. 顶层软件包命名规则：`com.yuxuanlei`
5. 所有和学生有关的表中用"俞轩磊"新建一条记录

### 功能特性

#### 核心功能
- 学生管理（CRUD + 分页 + 条件检索）
- 借书证管理（1:1 级联）
- 图书管理（CRUD + 分页 + 条件检索）
- 借阅管理（1:N 级联）
- 课程管理（CRUD + 分页）
- 选课管理（M:N 级联）
- 综合查询（级联查询）

#### 技术亮点
- 纯 MyBatis 实现（无 Spring Boot，无 MyBatis-Plus）
- 完整的 Service + Mapper + Entity 分层架构
- TransactionTemplate 统一事务管理（commit/rollback 策略）
- 完善的输入校验和异常处理
- 自定义分页查询和条件检索
- 命令行交互式菜单（8个功能模块）
- JUnit 5 单元测试（覆盖所有级联场景）
- Docker 多阶段构建（支持 ARM 和 X86）
- 数据库级联删除（ON DELETE CASCADE）

### 数据库设计

#### 表结构
- `student` - 学生表
- `library_card` - 借书证表（1:1 外键关联 student）
- `book` - 图书表
- `book_borrow` - 借阅记录表（1:N 外键关联 student 和 book）
- `course` - 课程表
- `student_course` - 学生选课表（M:N 中间表）

#### 级联关系
```
Student (1) ←→ (1) LibraryCard
Student (1) ←→ (N) BookBorrow
Student (1) ←→ (N) borrowedBooks（List<Book>，Prompt「学生和所借图书」一对多级联）
Student (M) ←→ (N) Course (通过 student_course 中间表)
```

### 测试用例

运行单元测试（需先启动 MySQL）：

- **Docker 环境**：`docker-compose up -d` 后，MySQL 首次启动会自动执行 `schema.sql`
- **本地 MySQL**：需先初始化，再运行测试
  ```bash
  cd backend
  # 初始化测试数据
  ./init-db.sh      # Linux/Mac
  .\init-db.ps1    # Windows
  # 运行测试
  set DB_HOST=localhost   # Windows: $env:DB_HOST="localhost"
  mvn test
  ```

`testFullCascadeQuery` 依赖预置数据（俞轩磊 2024001），若未初始化则自动跳过并提示。

测试覆盖：
- 学生 CRUD 测试
- 边界情况测试（空值、非法参数、分页边界、重复数据等）
- 1:1 级联测试（学生-借书证）
- 1:N 级联测试（学生-借阅记录）
- M:N 级联测试（学生-课程）
- 级联删除测试
- 分页查询测试
- 条件检索测试

### 命令行菜单

系统提供交互式命令行界面：

```
========================================
    图书管理系统 v1.0
========================================
1. 学生管理
2. 借书证管理
3. 图书管理
4. 借阅管理
5. 课程管理
6. 选课管理
7. 综合查询（级联查询）
0. 退出系统
========================================
```

### 项目说明

项目为标准 Maven 结构。

| 内容 | 位置 |
|------|------|
| **Java/Maven/MyBatis 项目**（源代码、pom.xml、配置等） | `backend/` 目录 |
| Docker 编排、README 等 | 项目根目录 |
| 编译、测试、打包、IDEA 导入 | 均在 `backend/` 目录下执行 |

> **重要**：IDEA 导入时请选择 **`backend/`** 目录，而非项目根目录。根目录无 `pom.xml`，导入会失败。详见上文「IntelliJ IDEA 导入」。

### 项目结构

```
<项目根目录>/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yuxuanlei/
│   │   │   │   ├── entity/          # 实体类（Student, LibraryCard, Book, BookBorrow, Course, StudentCourse）
│   │   │   │   ├── enums/           # 枚举（CardStatus, BorrowStatus）
│   │   │   │   ├── mapper/          # Mapper 接口（6 个）
│   │   │   │   ├── service/         # Service 层（6 个）
│   │   │   │   ├── cli/             # CLI 菜单（MainMenu, StudentMenu, LibraryCardMenu, BookMenu, BookBorrowMenu, CourseMenu, StudentCourseMenu, QueryMenu, CliErrorHandler）
│   │   │   │   ├── exception/       # 自定义异常（BusinessException, DataNotFoundException, DuplicateDataException）
│   │   │   │   ├── util/            # 工具类（MyBatisUtil, PageResult, ValidationUtil, TransactionTemplate, SqlSecurityUtil）
│   │   │   │   └── LibraryManagementApplication.java
│   │   │   └── resources/
│   │   │       ├── mapper/          # MyBatis XML（6 个）
│   │   │       ├── mybatis-config.xml
│   │   │       ├── jdbc.properties
│   │   │       ├── logback.xml
│   │   │       └── schema.sql       # 数据库初始化（含可重复执行测试数据）
│   │   └── test/java/com/yuxuanlei/service/
│   │       ├── StudentServiceTest.java
│   │       ├── BookServiceTest.java
│   │       ├── CascadeTest.java     # 级联关系测试
│   │       ├── PaginationTest.java  # 分页测试
│   │       └── BoundaryTest.java    # 边界情况测试
│   ├── docs/
│   │   ├── project_design.md
│   │   ├── API_DOCUMENTATION.md
│   │   └── SQL_SECURITY.md
│   ├── pack.sh / pack.ps1
│   ├── start.sh / start.ps1 / start.bat
│   ├── init-db.sh / init-db.ps1
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── library-management.iml
│   ├── .idea/
│   └── pom.xml
├── docker-compose.yml
├── .gitignore
└── README.md
```

### 技术架构

#### 核心技术栈
- **Java**: 17
- **MyBatis**: 3.5.13（纯 MyBatis，无 Spring，无 MyBatis-Plus）
- **MySQL**: 8.0
- **日志**: SLF4J 2.0.9 + Logback 1.4.11
- **测试**: JUnit 5.10.0
- **构建**: Maven 3.9+

#### 架构特点
1. **纯 MyBatis 实现**
   - 手动管理 SqlSession 生命周期
   - 显式事务控制（commit/rollback）
   - 自定义连接池配置

2. **完善的异常处理**
   - BusinessException - 业务异常基类
   - DataNotFoundException - 数据不存在异常
   - DuplicateDataException - 数据重复异常

3. **输入校验**
   - ValidationUtil 统一校验工具
   - 所有 Service 方法入参校验
   - 清晰的错误提示信息

4. **事务管理**
   - 所有写操作使用 SqlSession 事务
   - 异常自动回滚
   - 成功手动提交

5. **分页查询**
   - 自定义 PageResult 分页结果类
   - 支持 LIMIT OFFSET 分页
   - 返回总记录数和总页数

#### 代码结构

**Entity 层**（6个实体类）
- Student, LibraryCard, Book, BookBorrow, Course, StudentCourse
- 纯 POJO，手动 getter/setter

**Mapper 层**（6个接口 + 6个XML）
- 接口定义方法签名
- XML 实现 SQL 语句
- 支持 ResultMap 级联查询

**Service 层**（6个服务类）
- 业务逻辑处理
- SqlSession 事务管理
- 输入校验和异常处理

**CLI 层**（8个菜单类）
- 命令行交互界面
- Scanner 输入处理
- 格式化输出显示

**Util 层**（3个工具类）
- MyBatisUtil - SqlSession 工厂管理
- PageResult - 分页结果封装
- ValidationUtil - 输入校验工具

**Exception 层**（3个异常类）
- BusinessException - 业务异常
- DataNotFoundException - 数据不存在
- DuplicateDataException - 数据重复

### 本地开发

本项目为基于 Maven 的标准 MyBatis 项目，可直接使用 IntelliJ IDEA 导入。

#### IntelliJ IDEA 导入说明

**请导入 `backend/` 目录，不要导入项目根目录。**

项目已包含标准 IDEA 配置（`.idea/` 目录与 `library-management.iml`），可直接用 IDEA 打开 `backend/` 作为项目。根目录仅含 `docker-compose.yml`、`README.md` 等，不含 Maven 与 IDEA 配置。

**导入步骤：**
1. 打开 IDEA，选择 **File → Open**
2. 浏览到克隆后的项目，选择 **backend** 文件夹（内有 `pom.xml`）
3. 点击 **OK**，若提示 "Load Maven Project" 则点击 **Load**
4. 等待依赖下载完成，项目结构会自动识别
5. 运行：右键 `LibraryManagementApplication.java` → **Run 'LibraryManagementApplication.main()'**

#### 快速开始

1. **环境要求**
   - JDK 17+
   - Maven 3.9+
   - MySQL 8.0+

2. **数据库初始化**
```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

3. **配置数据库连接（本地开发）**

   Docker 环境下默认连接 `mysql:3306`。本地开发时需连接 `localhost:3306`，请设置环境变量：
   ```bash
   # Linux / Mac
   export DB_HOST=localhost

   # Windows CMD
   set DB_HOST=localhost

   # Windows PowerShell
   $env:DB_HOST="localhost"
   ```
   或修改 `backend/src/main/resources/jdbc.properties` 中的 `db.host=localhost`。

4. **编译项目**
```bash
cd backend
mvn clean compile
```

5. **运行测试**
```bash
mvn test
```

6. **启动应用**
```bash
mvn exec:java
# 或指定主类
mvn exec:java -Dexec.mainClass="com.yuxuanlei.LibraryManagementApplication"
```

### 文档

- [API 文档](backend/docs/API_DOCUMENTATION.md) - Service 层对外接口说明
- [项目设计文档](backend/docs/project_design.md) - 系统架构与数据库设计
- [SQL 安全规范](backend/docs/SQL_SECURITY.md) - MyBatis 防注入规范

### 重构说明

本项目已从 Spring Boot + MyBatis-Plus 重构为纯 MyBatis + Java SE 实现。

## 开发者

- 作者：俞轩磊
- 包名：com.yuxuanlei
- 版本：1.0.0

## License

MIT License
