package com.fuint.utils;

import java.security.MessageDigest;

/**
 * MD5加密工具
 *
 * Created by: FSQ
 * CopyRight https://www.fuint.cn
 */
public class MD5Util {
    private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int t;
        for (int i = 0; i < 16; i++) {// 16 == bytes.length;

            t = bytes[i];
            if (t < 0)
                t += 256;
            sb.append(hexDigits[(t >>> 4)]);
            sb.append(hexDigits[(t % 16)]);
        }
        return sb.toString();
    }

    public static String code(String input) {
        byte[] bytes = null;
        MessageDigest md = null;
        try {
            bytes = input.getBytes("utf-8");
            md = MessageDigest.getInstance(System.getProperty(
                    "MD5.algorithm", "MD5"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesToHex(md.digest(bytes));
    }
}