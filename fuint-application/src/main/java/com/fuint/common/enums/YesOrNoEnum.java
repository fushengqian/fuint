package com.fuint.common.enums;

/**
 * 是或否枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum YesOrNoEnum {
    YES("Y", "是"),
    NO("N", "否");

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
}
