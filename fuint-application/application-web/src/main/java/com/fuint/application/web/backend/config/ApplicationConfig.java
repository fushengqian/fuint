package com.fuint.application.web.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 加载applicationContext.xml
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Configuration
@ImportResource("classpath:/applicationContext.xml")
public class ApplicationConfig {
}
