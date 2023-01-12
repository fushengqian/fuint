package com.fuint.common.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 序列工具类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
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
}