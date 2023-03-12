package com.fuint.common.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 微信支付Bean
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
@PropertySource("file:${env.properties.path}/${env.profile}/application.properties")
@ConfigurationProperties(prefix = "wxpay")
public class WxPayBean {

    private String appId;
    private String appSecret;
    private String mchId;
    private String apiV2;
    private String certPath;
    private String domain; // 填写完整的回调地址

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getApiV2() {
        return apiV2;
    }

    public void setApiV2(String apiV2) {
        this.apiV2 = apiV2;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "WxPayBean [appId=" + appId + ", appSecret=" + appSecret + ", mchId=" + mchId + ", apiV2="
            + apiV2 + ", certPath=" + certPath + ", domain=" + domain + "]";
    }
}
