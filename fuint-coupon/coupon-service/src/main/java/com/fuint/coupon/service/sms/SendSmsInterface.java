package com.fuint.coupon.service.sms;

import java.util.List;
import java.util.Map;

/**
 * 发送短信接口
 * Created by zach on 20190821.
 */
public interface SendSmsInterface {

    /**
     * 发送短信方法
     * @param template_uname    短信模板英文名称
     * @param phones            手机号码集合
     * @return Map<Boolean,List<String>>    TRUE:推送成功的手机号码集合；
     *                                      FALSE:推送失败的手机号码集合
     * @throws Exception
     */
    Map<Boolean,List<String>> sendSms(String template_uname, List<String> phones, Map<String, String> contentParams) throws Exception;
}
