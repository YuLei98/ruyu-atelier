package icu.ruiyu.framework.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    Integer code;
    String message;
    public BusinessException(BusinessExceptionEnum businessExceptionEnum) {
        this.code = businessExceptionEnum.getCode();
        this.message = businessExceptionEnum.getMessage();
    }
}
