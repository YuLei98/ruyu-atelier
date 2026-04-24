package icu.ruiyu.framework.common.annotation;

import java.lang.annotation.*;

/**
 * 幂等性注解
 * 标注在 Controller 方法上，防止客户端重复提交
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等 key 的前缀，默认使用方法名
     */
    String keyPrefix() default "";

    /**
     * 过期时间（秒），默认 60 秒
     */
    int expireSeconds() default 60;

    /**
     * 提示信息
     */
    String message() default "请求已提交，请勿重复操作";
}
