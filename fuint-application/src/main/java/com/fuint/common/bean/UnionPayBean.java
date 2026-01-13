package com.fuint.common.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 云闪付支付Bean
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@Component
@PropertySource("file:${env.properties.path}/${env.profile}/application.properties")
@ConfigurationProperties(prefix = "union")
public class UnionPayBean {

    private String machId;
    private String key;
    private String serverUrl;
    private String domain;


    @Override
    public String toString() {
        return "UnionPayBean{" +
            "machId='" + machId + '\'' +
            ", key='" + key + '\'' +
            ", serverUrl='" + serverUrl + '\'' +
            ", domain='" + domain + '\'' +
            '}';
    }
}
