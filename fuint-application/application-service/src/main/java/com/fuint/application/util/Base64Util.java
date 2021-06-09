package com.fuint.application.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by zach on 2017/5/22.
 */
public class Base64Util {
    private static final Logger logger = LoggerFactory.getLogger(Base64Util.class);
    private static final String CHARSET = "UTF-8";

    public Base64Util() {
    }

    public static byte[] baseEncode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    public static String baseEncode(String s) {
        try {
            byte[] e = s.getBytes("UTF-8");
            return Base64.getEncoder().encodeToString(e);
        } catch (UnsupportedEncodingException var2) {
            logger.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static byte[] baseDecode(byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }

    public static String baseDecode(String s) {
        try {
            byte[] e = Base64.getDecoder().decode(s);
            return new String(e, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            logger.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static byte[] urlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().encode(bytes);
    }

    public static String urlEncode(String s) {
        try {
            byte[] e = s.getBytes("UTF-8");
            return Base64.getUrlEncoder().encodeToString(e);
        } catch (UnsupportedEncodingException var2) {
            logger.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static byte[] urlDecode(byte[] bytes) {
        return Base64.getUrlDecoder().decode(bytes);
    }

    public static String urlDecode(String s) {
        byte[] result = Base64.getUrlDecoder().decode(s);

        try {
            return new String(result, "UTF-8");
        } catch (UnsupportedEncodingException var3) {
            logger.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static byte[] mimeEncode(byte[] bytes) {
        return Base64.getMimeEncoder().encode(bytes);
    }

    public static String mimeEncode(String s) {
        try {
            byte[] e = s.getBytes("UTF-8");
            return Base64.getMimeEncoder().encodeToString(e);
        } catch (UnsupportedEncodingException var2) {
            logger.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static byte[] mimeDecode(byte[] bytes) {
        return Base64.getMimeDecoder().decode(bytes);
    }

    public static String mimeDecode(String s) {
        try {
            byte[] e = Base64.getMimeDecoder().decode(s);
            return new String(e, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            logger.error(var2.getMessage(), var2);
            return null;
        }
    }
}
