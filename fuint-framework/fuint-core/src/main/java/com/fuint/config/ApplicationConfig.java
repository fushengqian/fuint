package com.fuint.config;

import com.fuint.util.StringUtil;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 设置spring配置
 * Created by fsq on 19/11/29.
 */
@Configuration
@ComponentScan(basePackages = {"com.*"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.*.*.web.*"}))
@PropertySource(value = {"classpath:config/application.properties"})
@PropertySource("file:${env.properties.path}/${env.app.name}/${env.level}/application.properties")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
public class ApplicationConfig {


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        String envLevel = System.getProperty("env.level");
        String envPropertiesPath = System.getProperty("env.properties.path");
        System.out.println("######################应用启动成功！######################");
        System.out.println("#################环境级别:" + envLevel + "########################");
        System.out.println("#################Properties文件路径:" + envPropertiesPath + "########################");
        System.out.println("###################################################");
        if (StringUtil.isBlank(envLevel) || StringUtil.isBlank(envPropertiesPath)) {
            throw new RuntimeException("######系统参数错误.##########");
        }
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();

        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

}
