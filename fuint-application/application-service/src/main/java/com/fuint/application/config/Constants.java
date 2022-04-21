package com.fuint.application.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by FSQ
 * Contact wx fsq_better
 */
public class Constants {

    //第几页，默认第1页
    public static final int PAGE_NUMBER = 1;
    //每页记录数，默认20条
    public static final int PAGE_SIZE = 20;
    //停车场缴费模块ID
    public static final int PARK_MODULE_ID = 1000;

    /**
     * 系统配置, 从sys_config表中读取
     */
    public static Map<String, String> SYS_CONFIGS = new HashMap<String, String>();


    /**
     * 短信校验码超时时间(秒)
     */
    public static final int MOBILE_VCODE_TIMEOUT = 300;

    public static final int HTTP_RESPONSE_CODE_SUCCESS = 200;
    /**
     * 非法请求
     */
    public static final int HTTP_RESPONSE_CODE_UNLAWFUL_REQUEST = 110;


    public static final String SESSION_USER = "USER";
    public static final String SESSION_ADMIN_USER = "ADMIN_USER";

    public static final String MESSAGE_KEY_MOBILE_VCODE_MSG = "mobile.vcode.msg";

    /**
     * 数据状态
     */
    public static final int DATA_STATUS_NORMAL = 0;//正常状态
    public static final int DATA_STATUS_DELETED = 1;//删除状态
}
