package icu.ruiyu.framework.common;

import lombok.Data;

@Data
public class CommonResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(200);
        result.setMessage("SUCCESS");
        result.setData(data);
        return result;
    }

    public static <T> CommonResult<T> successMessage(String message) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static <T> CommonResult<T> error(int code, String message) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static <T> CommonResult<T> fail(String message) {
        CommonResult<T> result = new CommonResult<>();
        result.setCode(500);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}
