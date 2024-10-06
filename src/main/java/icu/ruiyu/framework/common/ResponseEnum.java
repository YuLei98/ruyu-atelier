package icu.ruiyu.framework.common;

import lombok.Getter;

@Getter
public enum ResponseEnum {
    SUCCESS(200, "SUCCESS"),
    FAIL(500, "FAILED"),

    HTTP_STATUS_200(200, "ok"),
    HTTP_STATUS_400(400, "request error"),
    HTTP_STATUS_401(401, "no authentication"),
    HTTP_STATUS_403(403, "no authorities"),
    HTTP_STATUS_500(500, "server error");

    private final Integer code;
    private final String message;
    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
