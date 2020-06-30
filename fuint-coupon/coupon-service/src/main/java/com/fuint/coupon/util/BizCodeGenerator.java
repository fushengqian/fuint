package com.fuint.coupon.util;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 业务Code生成器
 * Created by zach on 2017/2/14.
 */
public class BizCodeGenerator {

    private static final Logger log = LoggerFactory.getLogger(BizCodeGenerator.class);


    private static String DATE_FORMAT="yyyyMMddHHmmssSSS";
    /**
     * 获取指定前缀的Code字符串
     * @param preString 前缀字符串
     * @return
     */
    public synchronized static String getPreCodeString(String preString) {
        StringBuffer result = new StringBuffer();
        if(StringUtils.isNotEmpty(preString)){
            result.append(preString);
        }

        String dateStr =  new SimpleDateFormat(DATE_FORMAT).format(new Date());
        result.append(dateStr);

        String randomStr = RandomStringUtils.randomNumeric(2);
        result.append(randomStr);
        return result.toString();
    }


    /**
     * 生成6位数字短信验证码
     * @param
     * @return
     */
    public synchronized static String getVerifyCode() {
        String verifyCode = getFixLenthString(6);//String.valueOf(new Random().nextInt(899999) + 100000);
        return verifyCode;
    }

    /*
     * 返回长度为【strLength】的随机数，在前面补0
     */
    private static String getFixLenthString(int strLength) {
        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }
}
