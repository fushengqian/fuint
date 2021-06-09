package com.fuint.application.enums;

/**
 * 订单状态枚举
 * <p/>
 * Created by zach on 2021/05/05.
 */
public enum OrderStatusEnum {

    CREATED("A", "待支付"),
    PAID("B", "已支付"),
    CANCEL("C", "已取消"),
    DELETED("D", "已删除");

    private String key;

    private String value;

    OrderStatusEnum(String key, String value) {
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
