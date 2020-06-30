package com.fuint.coupon.enums;

/**
 * 分组类型
 * <p/>
 * Created by zach on 2020/4/29.
 */
public enum GroupTypeEnum {

    COUPON("C", "优惠券"),
    PRESTORE("P", "预存卡");

    private String key;

    private String value;

    GroupTypeEnum(String key, String value) {
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
