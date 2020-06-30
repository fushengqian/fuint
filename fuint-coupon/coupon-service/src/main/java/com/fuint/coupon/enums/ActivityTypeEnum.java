package com.fuint.coupon.enums;

/**
 * 活动类型枚举
 * <p>
 * Created by hanxiaoqiang on 2017/7/20.
 */
public enum ActivityTypeEnum {

    PROMOTION("PROMOTION", "9"),

    COUPON("COUPON", "4");

    private String key;

    private String value;

    ActivityTypeEnum(String key, String value) {
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
