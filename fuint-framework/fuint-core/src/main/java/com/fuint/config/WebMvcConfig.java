package com.fuint.config;

import com.fuint.interceptor.SiteHandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * MVC 基础配置
 *
 * Created by hanxiaoqiang on 19/11/29.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.*.*.web")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    @Autowired
    private Environment env;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController("welcome").setViewName("login");
        registry.addViewController("index").setViewName("login");
    }


    @Bean
    public HandlerAdapter servletHandlerAdapter() {
        logger.info("=====registration HandlerAdapter");
        return new SimpleServletHandlerAdapter();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("=====registration StaticResourceHandlers");
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        logger.info("=====registration CommonsMultipartResolver");
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(104857600);
        commonsMultipartResolver.setMaxInMemorySize(4096);
        return commonsMultipartResolver;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver b = new SimpleMappingExceptionResolver();
        Properties mappings = new Properties();
        mappings.put("org.springframework.dao.DataAccessException", "error");
        b.setExceptionMappings(mappings);
        return b;
    }

    private StringHttpMessageConverter stringHttpMessageConverter() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.TEXT_HTML);
        stringHttpMessageConverter.setSupportedMediaTypes(mediaTypeList);
        return stringHttpMessageConverter;
    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypeList = new ArrayList<MediaType>();
        mediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypeList);
        return mappingJackson2HttpMessageConverter;
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(this.stringHttpMessageConverter());
        messageConverters.add(this.mappingJackson2HttpMessageConverter());
        requestMappingHandlerAdapter.setMessageConverters(messageConverters);
        return requestMappingHandlerAdapter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SiteHandlerInterceptor(env)).addPathPatterns("/**");
    }
}
