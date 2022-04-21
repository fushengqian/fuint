package com.fuint.application.enums;

/**
 * 卡券类型
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public enum CouponTypeEnum {
    COUPON("C", "优惠券"),
    PRESTORE("P", "预存卡"),
    TIMER("T", "集次卡");

    private String key;

    private String value;

    CouponTypeEnum(String key, String value) {
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
