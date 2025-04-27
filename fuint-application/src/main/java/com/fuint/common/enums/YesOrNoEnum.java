package com.fuint.common.enums;

/**
 * 是或否枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum YesOrNoEnum {
    YES("Y", "是"),
    NO("N", "否"),
    TRUE("true", "真"),
    FALSE("false", "假");

    private String key;

    private String value;

    YesOrNoEnum(String key, String value) {
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

    public static String getKey(String v) {
        for (YesOrNoEnum c : YesOrNoEnum.values()) {
            if (c.getValue().equals(v)) {
                return c.getKey();
            }
        }
        return null;
    }

    public static String getValue(String k) {
        for (YesOrNoEnum c : YesOrNoEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }
}
