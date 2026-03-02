# SQL 安全规范

## 规范说明

为预防 SQL 注入，Mapper 编写须遵循以下规则：

1. **禁止使用 `${}` 拼接用户输入**  
   所有来自请求、表单、URL 的参数必须使用 `#{}` 预编译。

2. **安全写法示例**

   ```xml
   <!-- 正确：使用 #{} -->
   <select id="selectByName" resultType="Student">
       SELECT * FROM student WHERE name = #{name} AND deleted = 0
   </select>

   <!-- 正确：LIKE 模糊查询使用 CONCAT + #{} -->
   <select id="search" resultType="Student">
       SELECT * FROM student
       WHERE deleted = 0 AND name LIKE CONCAT('%', #{name}, '%')
   </select>

   <!-- 错误：${} 会直接拼接，存在注入风险 -->
   <!-- SELECT * FROM student ORDER BY ${sortColumn} ${sortDir}  -->
   ```

3. **动态排序字段**  
   若需支持用户指定排序列，须通过 `SqlSecurityUtil.sanitizeOrderColumn()` 白名单校验后再传入，或使用固定列名。

4. **现有 Mapper 状态**  
   本项目 Mapper 均已检查，全部使用 `#{}` 预编译与静态 `ORDER BY`，无 `${}` 拼接用户输入，符合安全规范。新增 SQL 须保持此要求。
