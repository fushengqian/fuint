package com.fuint.application.util;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 序列工具类
 *
 * @author zach
 */
public class SeqUtil {

    /**
     * 产生字符串序列(长度:32)
     *
     * @return String 32位字符串序列
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("\\-", "").toUpperCase();
    }

    /**
     * 产生日期序列(长度:32)
     * [yyyyMMddHHmmssSSS+15位随机数]
     *
     * @return Long
     */
    public static BigInteger getTimeSeq() {
        String date = CommonUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmssSSS");
        StringBuilder sb = new StringBuilder();
        sb.append(date);
        int length = 32 - sb.length();
        String randNum = getRandomNumber(length);
        sb.append(randNum);
        return new BigInteger(sb.toString());
    }

    /**
     * 产生日期序列(长度:大于17位)
     * [yyyyMMddHHmmssSSS+15位随机数]
     *
     * @return Long
     */
    public static String getTimeSeq(int length) {
        String date = CommonUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmssSSS");
        StringBuilder sb = new StringBuilder();
        sb.append(date);
        length = length - sb.length();
        if (length > 0) {
            String randNum = getRandomNumber(length);
            sb.append(randNum);
        }
        return sb.toString();
    }

    /**
     * 根据客户自定义前缀获取序列(长度:32)
     * [前缀+yyyyMMddHHmmssSSS+随机数]
     *
     * @param prefix
     * @return String
     */
    public static String getCustSeq(String prefix) {
        String date = CommonUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmssSSS");
        StringBuilder sb = new StringBuilder(prefix);
        sb.append(date);
        int length = 32 - sb.length();
        String randNum = getRandomNumber(length);
        sb.append(randNum);
        return sb.toString();
    }

    /**
     * 产生指定长度随机字母
     *
     * @param length
     * @return String
     */
    public static String getRandomLetter(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int rang = 26;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(rand.nextInt(rang)));
        }
        return sb.toString();
    }

    /**
     * 产生指定长度随机数字
     *
     * @param length
     * @return String
     */
    public static String getRandomNumber(int length) {
        String base = "0123456789";
        int rang = 10;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(rand.nextInt(rang)));
        }
        return sb.toString();
    }

    /**
     * 产生指定区间的随机整数
     *
     * @param min
     * @param max
     * @return int
     */
    public static int getRandomNumber(int min, int max) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return rand.nextInt(min, max);
    }
}