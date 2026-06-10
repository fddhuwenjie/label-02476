package com.yuxuanlei.web.common;

/**
 * 统一 API 响应包装。
 * <p>code=0 表示成功，非 0 表示业务/系统异常。
 */
public class Result<T> {

    /** 业务状态码：0=成功；4xx=客户端错误；5xx=服务端错误 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 数据载荷 */
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
