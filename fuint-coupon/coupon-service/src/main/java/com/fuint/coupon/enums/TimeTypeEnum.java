package com.fuint.coupon.enums;

/**
 * 有效期类型枚举
 * Created by zach on 2017/5/11.
 */
public enum TimeTypeEnum {

    ABSOLUTE("0", "绝对时间"),
    RELATIVE("1", "相对时间");

    TimeTypeEnum(String key, String value) {
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
