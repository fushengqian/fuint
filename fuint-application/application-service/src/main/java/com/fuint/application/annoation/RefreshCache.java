package com.fuint.application.annoation;

import java.lang.annotation.*;

/**
 * 刷新Redis缓存
 * <p>
 * Created by hanxiaoqiang on 2017/3/12.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefreshCache {
    String type() default "";
}
