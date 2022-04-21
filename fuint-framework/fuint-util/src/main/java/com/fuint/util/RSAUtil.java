package com.fuint.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

//import org.apache.commons.io.FileUtils;

/**
 * 数字签名工具类
 * 非对称加/解密(支持大数据)
 *
 * @author fsq
 */
public class RSAUtil {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);
    //默认字符集
    private static final String CHARSET = "UTF-8";
    //RSA密钥长度
    private static final int KEY_LENGTH = 1024;
    //RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;
    // RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;
    //密钥生成算法
    private static String KEY_RSA = "RSA";
    //签名/验签算法
    private static String KEY_MD5WITHRSA = "MD5withRSA";

    /**
     * 签名方法
     *
     * @param data       待签名数据
     * @param privateKey 私钥串
     * @return String 签名后数据
     */
    public static String signData(String data, String privateKey) {
        try {
//            byte[] signByte = signData(data.getBytes(CHARSET), privateKey);
//            return new String(signByte, CHARSET);
            PrivateKey prikey = getPrivateKey(privateKey);
            Signature signet = Signature.getInstance("SHA1withRSA");
            signet.initSign(prikey);
            signet.update(data.getBytes("UTF-8"));
            return HexStringByte.byteToHex(signet.sign());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 签名方法
     *
     * @param data       待签名数据
     * @param privateKey 私钥串
     * @return byte[] 签名后数据
     */
    public static byte[] signData(byte[] data, String privateKey) {
        try {
            PrivateKey key = getPrivateKey(privateKey);
            Signature sign = Signature.getInstance(KEY_MD5WITHRSA);
            sign.initSign(key);
            sign.update(data);
            return Base64Util.baseEncode(sign.sign());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
//        String key = "HNAmianshui365";
//        PublicKey publicKey = getPublicKey(key);
//        System.out.println(publicKey.getAlgorithm());

//        Map<String, Object> stringObjectMap = initKey();
//        System.out.println(JSONUtil.toString(stringObjectMap));

//        KeyPairGenerator keyPairGen = KeyPairGenerator
//                .getInstance(KEY_RSA);
//        keyPairGen.initialize(1024);
//
//        KeyPair keyPair = keyPairGen.generateKeyPair();
//
//        // 公钥
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//
//        // 私钥
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//
//        String s = encryptByPublicKey("1234qwer!@#$", new Base64().encodeBase64String(publicKey.getEncoded()));
//        System.out.println(s);
//
//        String s1 = decryptByPrivateKey(s, new Base64().encodeBase64String(privateKey.getEncoded()));
//        System.out.println(s1);
        String s = encryptByPublicKey("1234qwer!@#$",RSAKeys.PUBLIC_KEY);
        System.out.println(s);
        String s1 = decryptByPrivateKey(s, RSAKeys.PRIVATE_KEY);
        System.out.println(s1);
    }

    /** */
    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_RSA);
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<>(2);

        keyMap.put("PUBLIC_KEY", publicKey);
        keyMap.put("PRIVATE_KEY", privateKey);
        return keyMap;
    }

    /**
     * 验签方法
     *
     * @param signData   签名数据
     * @param sourceData 源数据
     * @param publicKey  公钥串
     * @return boolean
     */
    public static boolean verifyData(String signData, String sourceData, String publicKey) {
        try {
            return verifyData(signData.getBytes(CHARSET), sourceData.getBytes(CHARSET), publicKey);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 验签方法
     *
     * @param signData   签名数据
     * @param sourceData 源数据
     * @param publicKey  公钥串
     * @return boolean
     */
    public static boolean verifyData(byte[] signData, byte[] sourceData, String publicKey) {
        try {
            PublicKey key = getPublicKey(publicKey);
            Signature sign = Signature.getInstance(KEY_MD5WITHRSA);
            sign.initVerify(key);
            sign.update(sourceData);
            return sign.verify(Base64Util.baseDecode(signData));
        } catch (SignatureException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 获取公钥.
     * */
    public static String getPublicKey() {
        return RSAKeys.PUBLIC_KEY;
    }

    /**
     * 非对称加密[公钥加密]
     *
     * @param data      待加密数据
     * @param publicKey 公钥串
     * @return String 加密后数据
     */
    public static String encryptByPublicKey(String data, String publicKey) {
        try {
            byte[] bytes = encryptByPublicKey(data.getBytes(CHARSET), publicKey);;
            String base64 = new String(bytes);
            base64 = base64.replace("\n", "").replace("\r", "");
            return base64;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    /**
     * 非对称加密[公钥加密]
     *
     * @param data      待加密数据
     * @return String 加密后数据
     */
    public static String encryptByPublicKey(String data) {
        try {
            byte[] bytes = encryptByPublicKey(data.getBytes(CHARSET), RSAKeys.PUBLIC_KEY);;
            String base64 = new String(bytes);
            base64 = base64.replace("\n", "").replace("\r", "");
            return base64;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    /**
     * 非对称加密[公钥加密]
     *
     * @param data      待加密数据
     * @param publicKey 公钥串
     * @return byte[] 加密后数据
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) {
        PublicKey key = getPublicKey(publicKey);
        return encrypt(data, key);
    }


    /**
     * 非对称解密[私钥解密]
     *
     * @param data       待解密数据
     * @param privateKey 私钥串
     * @return String 解密后数据
     */
    public static String decryptByPrivateKey(String data, String privateKey) {
        try {
            byte[] bytes = decryptByPrivateKey(data.getBytes(CHARSET), privateKey);
            return new String(bytes, CHARSET);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 非对称解密[私钥解密]
     *
     * @param data       待解密数据
     * @param privateKey 私钥串
     * @return byte[] 解密后数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKey) {
        PrivateKey key = getPrivateKey(privateKey);
        return decrypt(data, key);
    }

    /**
     * 生成密钥字符串
     *
     * @param key 公钥或私钥对象
     * @return String 公钥或私钥字符串
     */
    private static String getKeyString(Key key) {
        byte[] keyBytes = key.getEncoded();
        return new String(Base64Util.baseEncode(keyBytes));
    }

    /**
     * 生成公钥对象
     *
     * @param key 公钥串
     * @return PublicKey 公钥对象
     */
    private static PublicKey getPublicKey(String key) {
        byte[] keyBytes = Base64Util.baseDecode(key.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_RSA);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 生成私钥对象
     *
     * @param key 私钥串
     * @return PrivateKey 私钥对象
     */
    private static PrivateKey getPrivateKey(String key) {
        byte[] keyBytes = Base64Util.baseDecode(key.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_RSA);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 分段加密(支持大数据)
     *
     * @param data
     * @param key
     * @return byte[]
     */
    private static byte[] encrypt(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(KeyFactory.getInstance(KEY_RSA).getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = data.length;
            int offSet = 0;
            int i = 0;
            byte[] cache = null;
            while (len - offSet > 0) {
                if (len - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, len - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            return Base64Util.baseEncode(out.toByteArray());
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 分段解密(支持大数据)
     *
     * @param data
     * @param key
     * @return byte[]
     */
    private static byte[] decrypt(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(KeyFactory.getInstance(KEY_RSA).getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            data = Base64Util.baseDecode(data);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = data.length;
            int offSet = 0;
            int i = 0;
            byte[] cache = null;
            while (len - offSet > 0) {
                if (len - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, len - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            return out.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getMessage(), e);
        } catch (BadPaddingException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}