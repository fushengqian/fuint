package com.fuint.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 设置spring配置
 * Created by FSQ
 * Contact wx fsq_better
 */
@Configuration
@ComponentScan(basePackages = {"com.*"}, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.*.*.web.*"}))
@PropertySource(value = {"classpath:config/application.properties"})
@PropertySource("file:${env.properties.path}/${env.level}/application.properties")

@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
public class ApplicationConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        System.out.println("###################### 应用启动成功！######################");
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
