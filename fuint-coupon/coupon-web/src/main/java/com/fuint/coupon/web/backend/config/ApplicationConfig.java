package com.fuint.coupon.web.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 加载applicationContext.xml
 * Created by Administrator on 2017/2/22.
 */
@Configuration
@ImportResource("classpath:/applicationContext.xml")
public class ApplicationConfig {
}
