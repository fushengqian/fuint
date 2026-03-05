package com.fuint.common.enums;

/**
 * 标签规则时间范围枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum TagRuleTimeRangeEnum {
    ONE_MONTH("1month", "1个月内", 30),
    THREE_MONTH("3month", "3个月内", 90),
    SIX_MONTH("6month", "6个月内", 180),
    ONE_YEAR("1year", "1年内", 365),
    TWO_YEAR("2year", "2年内", 730),
    ALL("all", "全部", 0);

    private String key;
    private String value;
    private Integer days;

    TagRuleTimeRangeEnum(String key, String value, Integer days) {
        this.key = key;
        this.value = value;
        this.days = days;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Integer getDays() {
        return days;
    }
}
