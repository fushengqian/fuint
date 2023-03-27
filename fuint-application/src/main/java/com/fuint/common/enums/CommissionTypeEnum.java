package com.fuint.common.enums;

/**
 * 分佣类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CommissionTypeEnum {
    DISTRIBUTION("distribution", "会员分销"),
    STAFF("staff", "员工提成");

    private String key;

    private String value;

    CommissionTypeEnum(String key, String value) {
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
