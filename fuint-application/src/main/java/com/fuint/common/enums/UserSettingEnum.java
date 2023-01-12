package com.fuint.common.enums;

/**
 * 会员配置项枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum UserSettingEnum {
    GET_COUPON_NEED_PHONE("getCouponNeedPhone", "领券是否需要手机号码"),
    SUBMIT_ORDER_NEED_PHONE("submitOrderNeedPhone", "提交订单是否需要手机号码"),
    LOGIN_NEED_PHONE("loginNeedPhone", "登录是否需要手机号");

    private String key;

    private String value;

    UserSettingEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // 普通方法，通过key获取value
    public static String getValue(String k) {
        for (UserSettingEnum c : UserSettingEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (UserSettingEnum c : UserSettingEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
