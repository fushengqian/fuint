package com.fuint.repository.model.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    String prefix() default "";
    String key() default "";
}
