package com.fuint.application.web.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 加载 applicationContext.xml
 * Created by zach on 2017/2/22.
 */
@Configuration
@ImportResource("classpath:/applicationContext.xml")
public class ApplicationConfig {
}
