package com.fuint.coupon.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES对称加/解密工具类
 *
 * @author zach by 2019/09/05
 */
public class AESUtil {
    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);

    //密钥生成算法
    private static String KEY_AES = "AES";
    //密钥长度
    private static int KEY_LENGTH = 128;
    //默认字符集
    private static final String CHARSET = "UTF-8";

    /**
     * AES加密
     *
     * @param data     待加密的内容
     * @param password 加密密码
     * @return byte[]
     */
    public static String encrypt(byte[] data, String password) {
        try {
            SecretKeySpec key = getKey(password);
            Cipher cipher = Cipher.getInstance(KEY_AES);// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return HexBin.encode(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * AES加密
     *
     * @param data     待加密的内容
     * @param password 加密密码
     * @return String
     */
    public static String encrypt(String data, String password) {
        try {
            return encrypt(data.getBytes(CHARSET), password);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * AES解密
     *
     * @param data     待解密内容
     * @param password 解密密钥
     * @return byte[]
     */
    public static byte[] decrypt(byte[] data, String password) {
        try {
            SecretKeySpec key = getKey(password);
            Cipher cipher = Cipher.getInstance(KEY_AES);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            return cipher.doFinal(HexBin.decode(new String(data, CHARSET)));
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * AES解密
     *
     * @param data     待解密内容
     * @param password 解密密钥
     * @return String
     */
    public static String decrypt(String data, String password) {
        try {
            byte[] bytes = decrypt(data.getBytes(CHARSET), password);
            return new String(bytes, CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 通过密码计算key
     *
     * @param password 对称密码
     * @return SecretKeySpec 计算的128位随机key
     * @throws NoSuchAlgorithmException
     */
    private static SecretKeySpec getKey(String password) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
        kgen.init(KEY_LENGTH, new SecureRandom(password.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        return new SecretKeySpec(enCodeFormat, KEY_AES);
    }
}