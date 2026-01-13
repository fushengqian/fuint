package com.fuint.common.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 微信支付Bean
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
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


    @Override
    public String toString() {
        return "WxPayBean [appId=" + appId + ", appSecret=" + appSecret + ", mchId=" + mchId + ", apiV2="
            + apiV2 + ", certPath=" + certPath + ", domain=" + domain + "]";
    }
}
