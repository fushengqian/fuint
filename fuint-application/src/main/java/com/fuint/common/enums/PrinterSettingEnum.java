package com.fuint.common.enums;

/**
 * 云打印设置
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum PrinterSettingEnum {

    USER_NAME("userName", "用户名"),
    USER_KEY("userKey", "开发者密钥"),
    ENABLE("enable", "是否启用");

    private String key;

    private String value;

    PrinterSettingEnum(String key, String value) {
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
        for (PrinterSettingEnum c : PrinterSettingEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (PrinterSettingEnum c : PrinterSettingEnum.values()) {
            if (c.getValue().equals(v)) {
                return c.getKey();
            }
        }
        return null;
    }
}
