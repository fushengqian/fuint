package com.fuint.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量定义
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class Constants {

    // 第几页，默认第1页
    public static final int PAGE_NUMBER = 1;

    // 每页记录数，默认20条
    public static final int PAGE_SIZE = 20;

    // 读取数据最多行数
    public static final int MAX_ROWS = 2000;

    /**
     * 系统配置, 从setting表中读取
     */
    public static Map<String, String> SYS_CONFIGS = new HashMap<String, String>();

    public static final int HTTP_RESPONSE_CODE_PARAM_ERROR = 202;
    public static final int HTTP_RESPONSE_CODE_USER_NOT_EXIST = 402;
    public static final int HTTP_RESPONSE_CODE_USER_LOGIN_ERROR = 403;
    public static final int HTTP_RESPONSE_CODE_NOLOGIN = 1001;

    public static final String SESSION_USER = "USER";
    public static final String SESSION_ADMIN_USER = "ADMIN_USER";
}
