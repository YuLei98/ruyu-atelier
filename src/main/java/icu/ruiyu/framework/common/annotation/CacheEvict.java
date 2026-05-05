package icu.ruiyu.framework.common.annotation;

import java.lang.annotation.*;

/**
 * 缓存淘汰注解
 * 同时删除 L1 和 L2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {

    /**
     * 缓存分区名称
     */
    String value() default "default";

    /**
     * 缓存 key，支持 SpEL 表达式
     */
    String key() default "";

    /**
     * 是否淘汰所有 key
     */
    boolean allEntries() default false;
}
