package icu.ruiyu.framework.exception;

import lombok.Getter;

@Getter
public enum BusinessExceptionEnum {
    INVALID_PARAM(101, "invalid parameter")
    ;
    Integer code;
    String message;

    BusinessExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
