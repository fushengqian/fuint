package com.fuint.initializer;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * 相关监听配置
 * <p/>
 * Created by FSQ
 * Contact wx fsq_better
 */
@Order(1)
public class ApplictionInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        //默认加载classpath 下的 logback.xml
        servletContext.addListener(LogbackConfigListener.class);


    }
}
