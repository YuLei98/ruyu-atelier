package icu.ruiyu.framework.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonResult<T> {
    Integer code;
    String message;
    T data;

    public static <T> CommonResult<T> success() {
        return success(null);
    }

    public static <T> CommonResult<T> success(T data) {
        return CommonResult.<T>builder()
                .code(ResponseEnum.SUCCESS.getCode())
                .message(ResponseEnum.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static <T> CommonResult<T> fail(String message, T data) {
        return CommonResult.<T>builder()
                .code(ResponseEnum.FAIL.getCode())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> CommonResult<T> fail(String message) {
        return fail(message, null);
    }
}
