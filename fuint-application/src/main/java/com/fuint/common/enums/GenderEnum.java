package com.fuint.common.enums;

/**
 * 性别枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum GenderEnum {

    FEMALE(0, "女"),
    MAN(1, "男"),
    UNKNOWN(2, "未知");

    private Integer key;

    private String value;

    GenderEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
