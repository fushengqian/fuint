package com.fuint.base.annoation;

import java.lang.annotation.*;

/**
 * service 操作日志记录注解
 *
 * Created by FSQ
 * Contact wx fsq_better
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationServiceLog {
    String description() default "";
}
