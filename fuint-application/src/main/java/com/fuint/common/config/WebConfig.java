package com.fuint.common.config;

import com.fuint.common.web.AdminUserInterceptor;
import com.fuint.common.web.CommandInterceptor;
import com.fuint.common.web.ClientUserInterceptor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import javax.servlet.annotation.MultipartConfig;
import java.util.concurrent.TimeUnit;

/**
 * web配置
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Configuration
@EnableCaching
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
@MultipartConfig(location = "org.springframework.web.multipart.commons.CommonsMultipartResolver", maxFileSize = 10240)
public class WebConfig extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/", "classpath:/other-resources/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                .resourceChain(false)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
                .addTransformer(new CssLinkResourceTransformer());
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public CommandInterceptor commandInterceptor() {
        return new CommandInterceptor();
    }

    @Bean
    public AdminUserInterceptor adminUserInterceptor() {
        return new AdminUserInterceptor();
    }

    @Bean
    public ClientUserInterceptor portalUserInterceptor() {
        return new ClientUserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Command命令请求拦截器
        registry.addInterceptor(commandInterceptor())
                .addPathPatterns("/cmd/**");

        // 后台管理拦截器
        registry.addInterceptor(adminUserInterceptor())
                .addPathPatterns("/backendApi/**")
                .excludePathPatterns("/clientApi/captcha/**")
                .excludePathPatterns("/backendApi/captcha/**")
                .excludePathPatterns("/backendApi/userCoupon/exportList")
                .excludePathPatterns("/backendApi/login/**");

        // 前端接口系统拦截器
        registry.addInterceptor(portalUserInterceptor())
                .addPathPatterns("/clientApi/**")
                .excludePathPatterns("/clientApi/sign/**")
                .excludePathPatterns("/clientApi/page/home")
                .excludePathPatterns("/clientApi/captcha/**")
                .excludePathPatterns("/clientApi/goodsApi/**")
                .excludePathPatterns("/clientApi/cart/**")
                .excludePathPatterns("/clientApi/user/**")
                .excludePathPatterns("/clientApi/settlement/submit")
                .excludePathPatterns("/clientApi/system/config")
                .excludePathPatterns("/clientApi/pay/**")
                .excludePathPatterns("/clientApi/order/todoCounts")
                .excludePathPatterns("/clientApi/store/**")
                .excludePathPatterns("/clientApi/message/getOne")
                .excludePathPatterns("/clientApi/sms/sendVerifyCode");
    }
}
