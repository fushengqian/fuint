package com.fuint.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuint.common.Constants;
import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.UserInfo;
import nl.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 登录Token服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
public class TokenUtil {

    public static int TOKEN_OVER_TIME = 604800;

    /**
     * 生成token
     *
     * @param userAgent
     * @param userId
     * @return
     * */
    public static String generateToken(String userAgent, Integer userId) {
        StringBuilder token = new StringBuilder();
        UserAgent userAgent1 = UserAgent.parseUserAgentString(userAgent);
        if (userAgent1.getOperatingSystem().isMobileDevice()) {
            token.append("APP_");
        } else {
            token.append("PC_");
        }

        token.append(userId);
        token.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + "_");
        token.append(new Random().nextInt((999999 - 111111 + 1)) + 111111);

        String result = MD5Util.getMD5(token.toString());
        return result;
    }

    /**
     * 保存token
     * */
    public static void saveToken(UserInfo userInfo) {
        if (userInfo == null || userInfo.getToken() == null) {
            return;
        }
        RedisUtil.set(Constants.SESSION_USER + userInfo.getToken(), userInfo, TOKEN_OVER_TIME);
    }

    /**
     * 通过token获取后台登录信息
     *
     * @param token
     * @return
     * */
    public static UserInfo getUserInfoByToken(String token) {
        Object loginInfo = RedisUtil.get(Constants.SESSION_USER + token);
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfo userInfo = objectMapper.convertValue(loginInfo, UserInfo.class);
        if (userInfo != null && userInfo.getToken().equals(token)) {
            return userInfo;
        }
        return null;
    }

    /**
     * 检查是否登录
     *
     * @param token
     * @return
     * */
    public static boolean checkTokenLogin(String token) {
        UserInfo userInfo = RedisUtil.get(Constants.SESSION_USER + token);
        if (userInfo != null && userInfo.getToken().equals(token)) {
            return true;
        }
        return false;
    }

    /**
     * 删除登录信息
     *
     * @param token
     * @return
     * */
    public static boolean removeToken(String token) {
        RedisUtil.remove(token);
        return true;
    }

    /**
     * 保存后台登录token
     * @param accountInfo
     * @return
     * */
    public static void saveAccountToken(AccountInfo accountInfo) {
        if (accountInfo == null) {
            return;
        }
        RedisUtil.set(Constants.SESSION_ADMIN_USER + accountInfo.getToken(), accountInfo, TOKEN_OVER_TIME);
    }

    /**
     * 通过登录token获取后台登录信息
     *
     * @param token
     * @return
     * */
    public static AccountInfo getAccountInfoByToken(String token) {
        Object loginInfo = RedisUtil.get(Constants.SESSION_ADMIN_USER + token);
        ObjectMapper objectMapper = new ObjectMapper();
        AccountInfo accountInfo = objectMapper.convertValue(loginInfo, AccountInfo.class);
        if (accountInfo != null && accountInfo.getToken().equals(token)) {
            return accountInfo;
        }
        return null;
    }
}