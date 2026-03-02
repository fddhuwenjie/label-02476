package com.yuxuanlei.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SQL 安全工具类：防止 SQL 注入。
 * <p>
 * 规范：
 * - 所有用户输入必须使用 #{param} 预编译，禁止使用 ${param} 拼接
 * - 若必须动态拼接排序字段等标识符，须通过本类白名单校验
 */
public final class SqlSecurityUtil {

    private SqlSecurityUtil() {}

    /** 允许的排序列白名单（表.列 或 列名），防止 ORDER BY 注入 */
    private static final Set<String> ALLOWED_ORDER_COLUMNS = Arrays.stream(new String[]{
            "id", "name", "student_no", "age", "major", "create_time", "update_time",
            "book_name", "author", "isbn", "total_count", "available_count",
            "course_name", "course_code", "credits", "teacher",
            "borrow_date", "return_date", "status", "enroll_date", "score",
            "card_no", "issue_date", "expire_date"
    }).collect(Collectors.toSet());

    /**
     * 校验并返回安全的排序列名，仅允许白名单内列名。
     *
     * @param column 用户传入的排序列名
     * @return 若在白名单内返回该列名，否则返回默认 "create_time"
     * @throws IllegalArgumentException 若 column 为 null 或空
     */
    public static String sanitizeOrderColumn(String column) {
        if (column == null || column.trim().isEmpty()) {
            throw new IllegalArgumentException("排序列不能为空");
        }
        String col = column.trim().toLowerCase();
        // 排除 ORDER BY 注入：仅允许单列名，禁止 , ; -- 等
        if (col.contains(",") || col.contains(";") || col.contains("--") || col.contains(" ")) {
            return "create_time";
        }
        return ALLOWED_ORDER_COLUMNS.contains(col) ? col : "create_time";
    }

    /**
     * 校验排序方向，仅允许 ASC/DESC。
     */
    public static String sanitizeOrderDirection(String direction) {
        if (direction == null || direction.trim().isEmpty()) {
            return "DESC";
        }
        String d = direction.trim().toUpperCase();
        return "ASC".equals(d) ? "ASC" : "DESC";
    }
}
