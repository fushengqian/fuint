package com.fuint.base.annoation;

import java.lang.annotation.*;

/**
 * service 操作日志记录注解
 * <p/>
 * Created by hanxiaoqiang on 16/9/1.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationServiceLog {
    String description() default "";
}
