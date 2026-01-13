package com.fuint.common.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * 微信V3支付Bean
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
@PropertySource("file:${env.properties.path}/${env.profile}/application.properties")
@ConfigurationProperties(prefix = "v3")
@Data
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
