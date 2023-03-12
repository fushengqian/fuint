package com.fuint.common.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付Bean
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
@PropertySource("file:${env.properties.path}/${env.profile}/application.properties")
@ConfigurationProperties(prefix = "alipay")
public class AliPayBean {

    private String appId;
    private String privateKey;
    private String publicKey;
    private String appCertPath;
    private String aliPayCertPath;
    private String aliPayRootCertPath;
    private String serverUrl;
    private String domain;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAppCertPath() {
        return appCertPath;
    }

    public void setAppCertPath(String appCertPath) {
        this.appCertPath = appCertPath;
    }

    public String getAliPayCertPath() {
        return aliPayCertPath;
    }

    public void setAliPayCertPath(String aliPayCertPath) {
        this.aliPayCertPath = aliPayCertPath;
    }

    public String getAliPayRootCertPath() {
        return aliPayRootCertPath;
    }

    public void setAliPayRootCertPath(String aliPayRootCertPath) {
        this.aliPayRootCertPath = aliPayRootCertPath;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "AliPayBean{" +
            "appId='" + appId + '\'' +
            ", privateKey='" + privateKey + '\'' +
            ", publicKey='" + publicKey + '\'' +
            ", appCertPath='" + appCertPath + '\'' +
            ", aliPayCertPath='" + aliPayCertPath + '\'' +
            ", aliPayRootCertPath='" + aliPayRootCertPath + '\'' +
            ", serverUrl='" + serverUrl + '\'' +
            ", domain='" + domain + '\'' +
            '}';
    }
}
