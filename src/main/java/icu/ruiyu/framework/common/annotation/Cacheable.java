package icu.ruiyu.framework.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存读取注解
 * 二级缓存：L1 (Caffeine) -> L2 (Redis) -> DB
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    /**
     * 缓存分区名称
     */
    String value() default "default";

    /**
     * 缓存 key，支持 SpEL 表达式
     */
    String key() default "";

    /**
     * L1 (Caffeine) 过期时间（分钟），默认 10 分钟
     */
    int expireAfterWriteMinutes() default 10;
}
