package com.yuxuanlei.enums;

/**
 * 借书证状态枚举，对应 library_card.status 字段
 */
public enum CardStatus {
    /** 有效 */
    ACTIVE,
    /** 过期 */
    EXPIRED,
    /** 挂失 */
    SUSPENDED;

    /**
     * 校验字符串是否为合法状态值
     */
    public static boolean isValid(String value) {
        if (value == null || value.isEmpty()) return false;
        for (CardStatus s : values()) {
            if (s.name().equals(value)) return true;
        }
        return false;
    }

    /**
     * 解析字符串为枚举，非法时抛出异常
     */
    public static CardStatus from(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("借书证状态不能为空");
        }
        for (CardStatus s : values()) {
            if (s.name().equalsIgnoreCase(value.trim())) return s;
        }
        throw new IllegalArgumentException("借书证状态必须是 ACTIVE、EXPIRED 或 SUSPENDED，当前值：" + value);
    }
}
