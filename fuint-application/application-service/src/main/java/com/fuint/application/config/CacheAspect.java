package com.fuint.application.config;

import com.fuint.application.annoation.RefreshCache;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 缓存刷新AOP实现
 */
@Component
@Aspect
public class CacheAspect {

    private Logger LOGGER = LoggerFactory.getLogger(CacheAspect.class);

    //Service层切点
    @Pointcut("@annotation(com.fuint.application.annoation.RefreshCache)")
    public void serviceAspect() {
    }


    /**
     * service 方法前调用
     *
     * @param joinPoint
     */
    @Before("serviceAspect()")
    public void doBeforeService(JoinPoint joinPoint) {
    }

    /**
     * 方法后调用
     *
     * @param joinPoint
     */
    @AfterReturning("serviceAspect() && @annotation(refreshCache)")
    public void doAfterInService(JoinPoint joinPoint, RefreshCache refreshCache) {
        long startTimeMillis = System.currentTimeMillis();
        try {
            long endTimeMillis = System.currentTimeMillis();
            LOGGER.info("刷新缓存:类型为:{}. 刷新缓存完毕.总共耗时:{}", refreshCache.type(), endTimeMillis - startTimeMillis);
        } catch (Exception e) {
            LOGGER.error("刷新缓存:缓存刷新失败.类型为:{}.异常:{}", refreshCache.type(), e);
        }
    }
}
