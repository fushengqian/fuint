package com.fuint.base.shiro.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

/**
 * Shiro filter initializer
 * Created by hanxiaoqiang on 16/7/11.
 */
@Order(3)
public class ShiroInitializer implements WebApplicationInitializer {

    Logger logger = LoggerFactory.getLogger(ShiroInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("加载shiro过滤器，与spring集成");

        FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("shiroFilter", DelegatingFilterProxy.class);
        shiroFilter.setInitParameter("argetFilterLifecycl", "true");
        shiroFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
        shiroFilter.setAsyncSupported(true);
    }
}
