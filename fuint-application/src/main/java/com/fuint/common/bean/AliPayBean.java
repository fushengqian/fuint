package com.fuint.common.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * 支付宝支付Bean
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
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
