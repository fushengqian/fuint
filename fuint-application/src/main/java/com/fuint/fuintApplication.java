package com.fuint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

/**
 * 启动程序
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@EnableScheduling
@SpringBootApplication
public class fuintApplication {

    public static final String REWRITE_FILTER_NAME = "rewriteFilter";
    public static final String REWRITE_FILTER_CONF_PATH = "urlRewrite.xml";

    public static void main(String[] args) {
        SpringApplication.run(fuintApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean rewriteFilterConfig() {
        FilterRegistrationBean reg = new FilterRegistrationBean();
        reg.setName(REWRITE_FILTER_NAME);
        reg.setFilter(new UrlRewriteFilter());
        reg.addInitParameter("confPath", REWRITE_FILTER_CONF_PATH);
        reg.addInitParameter("confReloadCheckInterval", "-1");
        reg.addInitParameter("statusPath", "/redirect");
        reg.addInitParameter("statusEnabledOnHosts", "*");
        reg.addInitParameter("logLevel", "WARN");
        return reg;
    }
}
