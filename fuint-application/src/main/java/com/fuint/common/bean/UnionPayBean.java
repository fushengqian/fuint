package com.fuint.common.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 云闪付支付Bean
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
@PropertySource("file:${env.properties.path}/${env.profile}/application.properties")
@ConfigurationProperties(prefix = "union")
public class UnionPayBean {

    private String machId;
    private String key;
    private String serverUrl;
    private String domain;

    public String getMachId() {
        return machId;
    }

    public void setMachId(String machId) {
        this.machId = machId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
        return "UnionPayBean{" +
            "machId='" + machId + '\'' +
            ", key='" + key + '\'' +
            ", serverUrl='" + serverUrl + '\'' +
            ", domain='" + domain + '\'' +
            '}';
    }
}
