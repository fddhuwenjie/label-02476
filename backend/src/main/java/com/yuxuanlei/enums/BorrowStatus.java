package com.yuxuanlei.enums;

/**
 * 借阅记录状态枚举，对应 book_borrow.status 字段
 */
public enum BorrowStatus {
    /** 已借出 */
    BORROWED,
    /** 已归还 */
    RETURNED;

    /**
     * 校验字符串是否为合法状态值
     */
    public static boolean isValid(String value) {
        if (value == null || value.isEmpty()) return false;
        for (BorrowStatus s : values()) {
            if (s.name().equals(value)) return true;
        }
        return false;
    }

    /**
     * 解析字符串为枚举
     */
    public static BorrowStatus from(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("借阅状态不能为空");
        }
        for (BorrowStatus s : values()) {
            if (s.name().equalsIgnoreCase(value.trim())) return s;
        }
        throw new IllegalArgumentException("借阅状态必须是 BORROWED 或 RETURNED，当前值：" + value);
    }
}
