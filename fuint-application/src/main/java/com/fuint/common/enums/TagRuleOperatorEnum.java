package com.fuint.common.enums;

/**
 * 标签规则操作符枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum TagRuleOperatorEnum {
    GT("gt", "大于"),
    GTE("gte", "大于等于"),
    LT("lt", "小于"),
    LTE("lte", "小于等于"),
    EQ("eq", "等于"),
    BETWEEN("between", "区间"),
    NE("ne", "不等于");

    private String key;
    private String value;

    TagRuleOperatorEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
