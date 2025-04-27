package com.fuint.common.enums;

/**
 * 短信配置项枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum SmsSettingEnum {
    IS_CLOSE("isClose", "是否关闭短信功能"),
    ACCESS_KEY_ID("accessKeyId", "阿里云accessKeyId"),
    ACCESS_KEY_SECRET("accessKeySecret", "阿里云accessKeySecret"),
    SIGN_NAME("signName", "阿里云短信签名");

    private String key;

    private String value;

    SmsSettingEnum(String key, String value) {
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
            if (c.getValue().equals(v)) {
                return c.getKey();
            }
        }
        return null;
    }
}
