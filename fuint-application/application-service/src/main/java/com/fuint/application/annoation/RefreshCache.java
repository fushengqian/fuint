package com.fuint.application.annoation;

import java.lang.annotation.*;

/**
 * 刷新Redis缓存
 *
 * Created by FSQ
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefreshCache {
    String type() default "";
}
