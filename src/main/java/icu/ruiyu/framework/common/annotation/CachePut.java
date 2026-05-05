package icu.ruiyu.framework.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存更新注解
 * 执行方法后更新 L1 和 L2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CachePut {

    /**
     * 缓存分区名称
     */
    String value() default "default";

    /**
     * 缓存 key，支持 SpEL 表达式
     */
    String key() default "";
}
