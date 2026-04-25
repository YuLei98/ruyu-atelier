package icu.ruiyu.framework.exception;

/**
 * OAuth 认证异常
 */
public class OAuthException extends RuntimeException {

    public OAuthException(String message) {
        super(message);
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}