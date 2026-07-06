package com.fuint.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuint.common.Constants;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.dto.member.UserInfo;
import com.fuint.utils.StringUtil;
import nl.bitwalker.useragentutils.UserAgent;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 登录Token服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class TokenUtil {

    public static final int TOKEN_OVER_TIME = 604800;

    public static final String TOKEN_NAME = "Access-Token";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest();
    }

    /**
     * 获取后台登录用户信息
     * */
    public static AccountInfo getAccountInfo() {
        return getAccountInfoByToken(getCurrentRequest().getHeader(TOKEN_NAME));
    }

    /**
     * 获取会员登录信息
     * */
    public static UserInfo getUserInfo() {
        return getUserInfoByToken(getCurrentRequest().getHeader(TOKEN_NAME));
    }

    /**
     * 构建Token源字符串（各生成方法共用）
     */
    private static String buildTokenSource(String userAgent, Integer userId) {
        StringBuilder stringBuilder = new StringBuilder();
        UserAgent userAgent1 = UserAgent.parseUserAgentString(userAgent);
        if (userAgent1.getOperatingSystem().isMobileDevice()) {
            stringBuilder.append("APP_");
        } else {
            stringBuilder.append("PC_");
        }
        stringBuilder.append(userId);
        stringBuilder.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + "_");
        stringBuilder.append(SECURE_RANDOM.nextInt(900000) + 100000);
        return stringBuilder.toString();
    }

    /**
     * 生成token
     *
     * @param userAgent
     * @param userId
     * @return
     * */
    public static String generateToken(String userAgent, Integer userId) {
        String token = SHAUtil.sha256(buildTokenSource(userAgent, userId));

        UserInfo userLoginInfo = new UserInfo();
        userLoginInfo.setId(userId);
        userLoginInfo.setToken(token);
        saveToken(userLoginInfo);

        return token;
    }

    /**
     * 生成token
     *
     * @param userAgent
     * @param accountInfo
     * @return
     * */
    public static String generateToken(String userAgent, AccountInfo accountInfo) {
        String token = SHAUtil.sha256(buildTokenSource(userAgent, accountInfo.getId()));

        accountInfo.setToken(token);
        saveAccountToken(accountInfo);

        return token;
    }

    /**
     * 保存token
     *
     * @param userInfo
     * @return
     * */
    public static void saveToken(UserInfo userInfo) {
        if (userInfo == null || userInfo.getToken() == null || userInfo.getId() == null) {
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
        if (token == null || StringUtil.isEmpty(token)) {
            return null;
        }
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
        try {
            UserInfo userInfo = RedisUtil.get(Constants.SESSION_USER + token);
            if (userInfo != null && userInfo.getToken().equals(token)) {
                return true;
            }
        } catch (Exception e) {
            return false;
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
        AuthUserUtil.clean();
        return true;
    }

    /**
     * 保存后台登录token
     *
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
