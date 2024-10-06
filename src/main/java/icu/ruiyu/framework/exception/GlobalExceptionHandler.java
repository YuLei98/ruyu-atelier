package icu.ruiyu.framework.exception;

import icu.ruiyu.framework.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public CommonResult<String> handleHttpMessageNotReadableException(
            MissingServletRequestParameterException ex) {
        log.error("缺少请求参数，{}", ex.getMessage());
        return CommonResult.fail("缺少请求参数");
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<String> handleNullPointerException(
            NullPointerException ex) {
        log.error("空指针异常，{}", ex.getMessage());
        return CommonResult.fail("空指针异常");
    }


    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<String> handleNullPointerException(
            BusinessException ex) {
        log.error("无效参数，{}", ex.getMessage());
        return CommonResult.fail(ex.getMessage());
    }
}
