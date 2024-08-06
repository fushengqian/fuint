package com.fuint.common.enums;

/**
 * 预约订单状态枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum BookStatusEnum {
    CREATED("A", "已提交"),
    CONFIRM("B", "审核通过"),
    CANCEL("C", "审核未通过"),
    DELETE("D", "已删除"),
    COMPLETE("E", "已完成");

    private String key;

    private String value;

    BookStatusEnum(String key, String value) {
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
