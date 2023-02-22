package com.fuint.common.config;

import com.fuint.common.service.StoreService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
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

/**
 * 微信支付配置类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Configuration
public class WXPayConfigImpl implements WXPayConfig {

    private static final Logger logger = LoggerFactory.getLogger(WXPayConfigImpl.class);

    private String appId;

    private String mchId;

    private String key;

    private String callbackUrl;

    private String certPath;

    private static WXPayConfigImpl instance = new WXPayConfigImpl();

    @Autowired
    public static WXPayConfigImpl getInstance(Environment env, Integer storeId, StoreService storeService) throws BusinessCheckException {
       instance.appId = env.getProperty("weixin.pay.appId");
       instance.mchId = env.getProperty("weixin.pay.mchId");
       instance.key = env.getProperty("weixin.pay.key");
       instance.callbackUrl = env.getProperty("weixin.pay.callbackUrl");
       instance.certPath = env.getProperty("weixin.pay.certPath");

       if (storeId != null && storeId > 0) {
           MtStore mtStore = storeService.queryStoreById(storeId);
           if (mtStore.getWxMchId() != null && StringUtil.isNotEmpty(mtStore.getWxMchId()) && mtStore.getWxApiV2() != null && StringUtil.isNotEmpty(mtStore.getWxApiV2())) {
               instance.mchId = mtStore.getWxMchId();
               instance.key = mtStore.getWxApiV2();
           }
       }

       return instance;
    }

    @Override
    public String getAppID() {
        return appId;
    }

    @Override
    public String getMchID() {
        return mchId;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public InputStream getCertStream() {
        try {
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
        return callbackUrl;
    }
}
