package com.fuint.coupon.enums;

/**
 * 促销标签用途枚举
 * <p>
 * Created by hanxiaoqiang on 2017/4/13.
 */
public enum TagUsedEnum {

    ALL_PAGE("0","所有页面"),

    LIST_PAGE("1", "列表页面"),

    DETAIL_PAGE("2", "详情页面");

    private String key;

    private String value;

    TagUsedEnum(String key, String value) {
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
