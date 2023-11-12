package com.fuint.common.enums;

/**
 * 订单结算状态
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum SettleStatusEnum {
    WAIT("A", "待结算"),
    COMPLETE("B", "已结算");

    private String key;

    private String value;

    SettleStatusEnum(String key, String value) {
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
