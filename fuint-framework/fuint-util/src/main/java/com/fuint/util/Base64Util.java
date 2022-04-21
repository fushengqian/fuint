package com.fuint.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 基于Java8的Base64工具类实现,不依赖第三方包
 * [Basic编码:适用于标准编码]
 * [URL编码:适用于URL地址编码,自动替换掉URL中不能出现的"/"等字符]
 * [MIME编码:适用于MIME编码,使用基本的字母数字产生BASE64输出,每一行输出不超过76个字符，而且每行以“\r\n”符结束]
 * Created by FSQ
 * Contact wx fsq_better
 */
public class Base64Util {
    private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);

    private static final String CHARSET = "UTF-8";//默认字符集

    /**
     * 基本Base64编码
     * @param bytes
     * @return byte[]
     */
    public static byte[] baseEncode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    /**
     * 基本Base64编码
     *
     * @param s
     * @return String
     */
    public static String baseEncode(String s) {
        try {
            byte[] bytes = s.getBytes(CHARSET);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 基本Base64解码
     *
     * @param bytes
     * @return byte[]
     */
    public static byte[] baseDecode(byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }

    /**
     * 基本Base64解码
     *
     * @param s
     * @return String
     */
    public static String baseDecode(String s) {
        try {
            byte[] result = Base64.getDecoder().decode(s);
            return new String(result, CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * URL编码
     *
     * @param bytes
     * @return byte[]
     */
    public static byte[] urlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().encode(bytes);
    }

    /**
     * URL编码
     *
     * @param s
     * @return String
     */
    public static String urlEncode(String s) {
        try {
            byte[] bytes = s.getBytes(CHARSET);
            return Base64.getUrlEncoder().encodeToString(bytes);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * URL解码
     *
     * @param bytes
     * @return byte[]
     */
    public static byte[] urlDecode(byte[] bytes) {
        return Base64.getUrlDecoder().decode(bytes);
    }

    /**
     * URL解码
     *
     * @param s
     * @return String
     */
    public static String urlDecode(String s) {
        byte[] result = Base64.getUrlDecoder().decode(s);
        try {
            return new String(result, CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * MIME编码
     *
     * @param bytes
     * @return byte[]
     */
    public static byte[] mimeEncode(byte[] bytes) {
        return Base64.getMimeEncoder().encode(bytes);
    }

    /**
     * MIME编码
     *
     * @param s
     * @return String
     */
    public static String mimeEncode(String s) {
        try {
            byte[] bytes = s.getBytes(CHARSET);
            return Base64.getMimeEncoder().encodeToString(bytes);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * MIME解码
     *
     * @param bytes
     * @return byte[]
     */
    public static byte[] mimeDecode(byte[] bytes) {
        return Base64.getMimeDecoder().decode(bytes);
    }

    /**
     * MIME解码
     *
     * @param s
     * @return String
     */
    public static String mimeDecode(String s) {
        try {
            byte[] result = Base64.getMimeDecoder().decode(s);
            return new String(result, CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
