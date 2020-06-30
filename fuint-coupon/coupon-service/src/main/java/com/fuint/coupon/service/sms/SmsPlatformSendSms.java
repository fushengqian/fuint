package com.fuint.coupon.service.sms;

import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 短信平台 - 发送短信
 * Created by zach on 20190821.
 */
@Service
public class SmsPlatformSendSms implements SendSmsInterface {

    private static final Logger logger = LoggerFactory.getLogger(SmsPlatformSendSms.class);
    @Resource
    private SmsPlatformService smsPlatformService;

    @Autowired
    private Environment env;

    @Override
    public Map<Boolean,List<String>> sendSms(String templateUname, List<String> phones, Map<String, String> contentParams) throws Exception {
        logger.info("使用短信平台发送短信.....");
        Integer mode=Integer.parseInt(env.getProperty("ALIYUN.SMS.mode"));
        if (templateUname != null && !CollectionUtils.isEmpty(phones)) {
            Map<Boolean,List<String>> result = new HashMap<>();
            try{
                if (mode != null && mode.intValue() == 1) {
                    // 手机号以","分隔拼接
                    String mobilePhones = phones.stream().collect(Collectors.joining(","));
                    MessageResInfo res = smsPlatformService.sendMessage(mobilePhones, templateUname, contentParams);
                    result.put(res.getResult(),phones);
                } else {
                    result.put(Boolean.TRUE,phones);
                    logger.info("模拟短信平台发送短信.....");
                }
            } catch (Exception e) {
                result.put(Boolean.FALSE,phones);
                logger.error("推送至短信平台出错...参数[smscontent={},phones={}]", templateUname, phones);
                logger.error(e.getMessage(),e);
            }
            return result;
        } else {
            throw new BusinessCheckException("推送短信平台,短信内容或者手机号码为空，请确认!");
        }
    }
}
