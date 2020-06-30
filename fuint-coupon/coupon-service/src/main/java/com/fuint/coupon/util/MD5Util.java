package com.fuint.coupon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by gang.wang on 2017/5/22.
 */
public class MD5Util {
    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);
    private static final String CHARSET = "UTF-8";

    public MD5Util() {
    }

    public static byte[] getMD5(byte[] bytes) {
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            return e.digest(bytes);
        } catch (NoSuchAlgorithmException var2) {
            logger.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static String getMD5(String str) {
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            byte[] bytes = e.digest(str.getBytes("UTF-8"));
            byte[] result = Base64Util.baseEncode(bytes);
            return new String(result);
        } catch (NoSuchAlgorithmException var4) {
            logger.error(var4.getMessage(), var4);
        } catch (UnsupportedEncodingException var5) {
            logger.error(var5.getMessage(), var5);
        }

        return null;
    }

    public static Boolean validateMD5(byte[] cleartext, byte[] ciphertext) {
        try {
            String e = new String(cleartext, "UTF-8");
            String cipher = new String(ciphertext, "UTF-8");
            return validateMD5((String)e, (String)cipher);
        } catch (UnsupportedEncodingException var4) {
            logger.error(var4.getMessage(), var4);
            return Boolean.valueOf(false);
        }
    }

    public static Boolean validateMD5(String cleartext, String ciphertext) {
        String str = getMD5((String)cleartext);
        return str != null && str.equals(ciphertext)?Boolean.valueOf(true):Boolean.valueOf(false);
    }
}
