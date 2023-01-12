package com.fuint.common.enums;

/**
 * 订单模式
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum OrderModeEnum {
    EXPRESS("express", "配送"),
    ONESELF("oneself", "自取");

    private String key;

    private String value;

    OrderModeEnum(String key, String value) {
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
