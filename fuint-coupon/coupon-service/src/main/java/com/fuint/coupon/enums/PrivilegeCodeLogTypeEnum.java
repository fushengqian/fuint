package com.fuint.coupon.enums;

/**
 * 特权码操作类型枚举
 * Created by zach on 2017/5/18.
 */
public enum PrivilegeCodeLogTypeEnum {

    RECEIVE("1", "领取特权码"),

    USE("2", "使用特权码"),

    FREEZE("3", "冻结特权码"),

    FREE("4", "解冻特权码");

    PrivilegeCodeLogTypeEnum(String key, String value) {
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
