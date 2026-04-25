package icu.ruiyu.framework.exception;

/**
 * 限流异常
 * 当 API 请求超出限流阈值时抛出
 */
public class RateLimitException extends RuntimeException {

    private final int code = 429;

    public RateLimitException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }
}
