package com.fuint.coupon.enums;

/**
 * 特权码状态枚举
 * Created by zach on 2017/5/10.
 */
public enum PrivilegeCodeStatusEnum {

    NOT_RECEIVE("0", "未领取"),
    NOT_USED("1", "已领取未使用"),
    USED("2", "已使用"),
    FREEZE("3", "已冻结"),
    DELETE("4","已删除");

    PrivilegeCodeStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    private String key;

    private String value;

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
