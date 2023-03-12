package com.fuint.common.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 微信V3支付Bean
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
@PropertySource("file:${env.properties.path}/${env.profile}/application.properties")
@ConfigurationProperties(prefix = "v3")
public class WxPayV3Bean {

    private String appId;
    private String keyPath;
    private String certPath;
    private String certP12Path;
    private String platformCertPath;
    private String mchId;
    private String apiKey;
    private String apiKey3;
    private String domain;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertP12Path() {
        return certP12Path;
    }

    public void setCertP12Path(String certP12Path) {
        this.certP12Path = certP12Path;
    }

    public String getPlatformCertPath() {
        return platformCertPath;
    }

    public void setPlatformCertPath(String platformCertPath) {
        this.platformCertPath = platformCertPath;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey3() {
        return apiKey3;
    }

    public void setApiKey3(String apiKey3) {
        this.apiKey3 = apiKey3;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "WxPayV3Bean{" +
            "keyPath='" + keyPath + '\'' +
            ", certPath='" + certPath + '\'' +
            ", certP12Path='" + certP12Path + '\'' +
            ", platformCertPath='" + platformCertPath + '\'' +
            ", mchId='" + mchId + '\'' +
            ", apiKey='" + apiKey + '\'' +
            ", apiKey3='" + apiKey3 + '\'' +
            ", domain='" + domain + '\'' +
            '}';
    }
}
