package com.yuxuanlei.cli;

import com.yuxuanlei.exception.BusinessException;
import com.yuxuanlei.exception.DataNotFoundException;
import com.yuxuanlei.exception.DuplicateDataException;

/**
 * CLI 层统一错误提示处理
 * 将异常转换为用户友好的提示信息
 */
public final class CliErrorHandler {
    
    private CliErrorHandler() {}
    
    /**
     * 将异常转换为用户友好的错误提示
     */
    public static String getUserFriendlyMessage(Throwable e) {
        if (e == null) {
            return "发生未知错误，请重试。";
        }
        
        if (e instanceof DataNotFoundException) {
            return "✗ 未找到相关数据：" + e.getMessage() + "，请检查输入是否正确。";
        }
        if (e instanceof DuplicateDataException) {
            return "✗ 数据已存在：" + e.getMessage() + "，请勿重复操作。";
        }
        if (e instanceof IllegalArgumentException) {
            return "✗ 输入有误：" + e.getMessage() + "，请按要求重新输入。";
        }
        if (e instanceof BusinessException) {
            return "✗ 操作失败：" + e.getMessage();
        }
        if (e instanceof NumberFormatException) {
            return "✗ 请输入有效的数字。";
        }
        
        String msg = e.getMessage();
        if (msg != null && !msg.isEmpty()) {
            return "✗ 操作失败：" + msg + "。如有疑问请联系管理员。";
        }
        return "✗ 操作失败，请重试。如有疑问请联系管理员。";
    }
}
