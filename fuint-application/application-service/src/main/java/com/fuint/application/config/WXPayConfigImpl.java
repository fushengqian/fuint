package com.fuint.application.config;

import com.github.wxpay.sdk.WXPayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Configuration
public class WXPayConfigImpl implements WXPayConfig {

    private static final Logger logger = LoggerFactory.getLogger(WXPayConfigImpl.class);

    @Autowired
    private Environment env;

    @Override
    public String getAppID() {
        String appId = env.getProperty("weixin.pay.appId");
        return appId;
    }

    @Override
    public String getMchID() {
        String mchId = env.getProperty("weixin.pay.mchId");
        return mchId;
    }

    @Override
    public String getKey() {
        String key = env.getProperty("weixin.pay.key");
        return key;
    }

    @Override
    public InputStream getCertStream() {
        try {
            String certPath = env.getProperty("weixin.pay.certPath");
            File file = new File(certPath);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    public String getCallbackUrl() {
        String callbackUrl = env.getProperty("weixin.pay.callbackUrl");
        return callbackUrl;
    }
}
