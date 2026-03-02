package com.yuxuanlei.exception;

/**
 * 数据重复异常
 */
public class DuplicateDataException extends BusinessException {
    
    public DuplicateDataException(String message) {
        super("DUPLICATE_DATA", message);
    }
}
