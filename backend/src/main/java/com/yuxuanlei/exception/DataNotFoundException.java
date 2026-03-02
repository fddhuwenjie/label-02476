package com.yuxuanlei.exception;

/**
 * 数据不存在异常
 */
public class DataNotFoundException extends BusinessException {
    
    public DataNotFoundException(String message) {
        super("DATA_NOT_FOUND", message);
    }
}
