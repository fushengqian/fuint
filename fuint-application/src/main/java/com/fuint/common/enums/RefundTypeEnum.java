package com.fuint.common.enums;

/**
 * 售后类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum RefundTypeEnum {

    RETURN("return", "退货退款"),
    REJECT("exchange", "换货");

    private String key;

    private String value;

    RefundTypeEnum(String key, String value) {
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
