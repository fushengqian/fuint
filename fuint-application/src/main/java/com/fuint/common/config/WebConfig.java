package com.fuint.common.config;

import com.fuint.common.web.AdminUserInterceptor;
import com.fuint.common.web.CommandInterceptor;
import com.fuint.common.web.ClientUserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.concurrent.TimeUnit;

/**
 * web配置
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Configuration
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

        registry.addResourceHandler("/**").addResourceLocations(
                "classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
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
        // Command
        registry.addInterceptor(commandInterceptor())
                .addPathPatterns("/cmd/**");

        // 后台拦截
        registry.addInterceptor(adminUserInterceptor())
                .addPathPatterns("/backendApi/**")
                .excludePathPatterns("/clientApi/captcha/**")
                .excludePathPatterns("/backendApi/captcha/**")
                .excludePathPatterns("/backendApi/userCoupon/exportList")
                .excludePathPatterns("/backendApi/login/**");

        // 客户端拦截
        registry.addInterceptor(portalUserInterceptor())
                .addPathPatterns("/clientApi/**")
                .excludePathPatterns("/clientApi/sign/**")
                .excludePathPatterns("/clientApi/page/home")
                .excludePathPatterns("/clientApi/captcha/**")
                .excludePathPatterns("/clientApi/goodsApi/**")
                .excludePathPatterns("/clientApi/cart/**")
                .excludePathPatterns("/clientApi/user/**")
                .excludePathPatterns("/clientApi/settlement/submit")
                .excludePathPatterns("/clientApi/pay/**")
                .excludePathPatterns("/clientApi/order/todoCounts")
                .excludePathPatterns("/clientApi/store/**")
                .excludePathPatterns("/clientApi/message/getOne")
                .excludePathPatterns("/clientApi/message/wxPush")
                .excludePathPatterns("/clientApi/sms/sendVerifyCode");
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }
}
