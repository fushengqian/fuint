package com.fuint.common.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.InputStream;
import java.util.Properties;

/**
 * 图形验证码组件配置
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Configuration
public class CaptchaConfig {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaConfig.class);

    /**
     * 验证码
     */
    @Bean
    public DefaultKaptcha defaultCaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties property = new Properties();
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("captcha-conf.properties");
            property.load(inputStream);
            Config config = new Config(property);
            defaultKaptcha.setConfig(config);
        } catch (Exception e) {
            logger.error("Kaptcha properties load error {}", e);
            throw new RuntimeException("Kaptcha properties load error");
        }
        return defaultKaptcha;
    }
}
