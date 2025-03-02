
package com.ant.bmr.config.common.result;

import java.io.Serializable;

import com.ant.bmr.config.common.context.GlobalContext;
import lombok.Data;
import org.slf4j.MDC;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -8151474107878777391L;
    private int code;
    private String msg;
    private T data;
    private Boolean isSuccess;
    private String traceId = MDC.get(GlobalContext.TRACE_ID);

    public Result() {
    }

    public Result(int code, String msg, T data, Boolean isSuccess) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.isSuccess = isSuccess;
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(int code, String msg, Boolean isSuccess) {
        this.code = code;
        this.msg = msg;
        this.isSuccess = isSuccess;
    }

    public static Boolean isSuccess(int code) {
        return code == 0;
    }

    public static <T> Result<T> success() {
        return new Result<>(0, "Success", null, isSuccess(0));
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "Success", data, isSuccess(0));
    }

    public static <T> Result<T> success(T data, String msg) {
        return new Result<>(0, msg, data, isSuccess(0));
    }

    public static <T> Result<T> success(String msg) {
        return new Result<>(0, msg, null, isSuccess(0));
    }

    public static <T> Result<T> success(int code, String msg) {
        return new Result<>(code, msg, null, isSuccess(code));
    }

    public static <T> Result<T> failure(int code, String msg) {
        return new Result<>(code, msg, isSuccess(code));
    }

    public static <T> Result<T> failure(String msg) {
        return new Result<>(500, msg, isSuccess(500));
    }
}