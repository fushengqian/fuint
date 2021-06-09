package com.fuint.application.enums;

/**
 * 订单类型
 * <p/>
 * Created by zach on 2021/5/05.
 */
public enum OrderTypeEnum {
    PRESTORE("prestore", "预存卡订单"),
    MEMBER("member", "会员升级订单"),
    RECHARGE("recharge", "充值订单"),
    GOOGS("goods", "商品订单");

    private String key;

    private String value;

    OrderTypeEnum(String key, String value) {
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
