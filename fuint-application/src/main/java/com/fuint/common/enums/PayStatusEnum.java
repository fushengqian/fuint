package com.fuint.common.enums;

/**
 * 支付状态
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum PayStatusEnum {
    WAIT("A", "待支付"),
    SUCCESS("B", "已支付");

    private String key;

    private String value;

    PayStatusEnum(String key, String value) {
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
