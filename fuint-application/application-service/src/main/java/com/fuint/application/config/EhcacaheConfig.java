package com.fuint.application.config;

import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

/**
 * Created by zach on 2017/6/13.
 */
@Configuration
public class EhcacaheConfig {

    @Autowired
    private Environment env;

    @Bean
    public CacheManager getCacheManager() {
        String cacheProperties = env.getProperty("sys.properties.path") + File.separator + env.getProperty("env.app.name") + File.separator + env.getProperty("sys.level") + File.separator + "ehcache.xml";
        CacheManager cacheManager = new CacheManager(cacheProperties);
        return cacheManager;
    }
}
