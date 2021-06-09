package com.fuint.application.enums;

/**
 * 状态枚举
 * <p/>
 * Created by zach on 2019/09/05.
 */
public enum StatusEnum {

    ENABLED("A", "启用"),
    EXPIRED("C", "过期"),
    DISABLE("D", "删除"),
    FORBIDDEN("N", "禁用"),
    UnAudited("U", "待审核");

    private String key;

    private String value;

    StatusEnum(String key, String value) {
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
