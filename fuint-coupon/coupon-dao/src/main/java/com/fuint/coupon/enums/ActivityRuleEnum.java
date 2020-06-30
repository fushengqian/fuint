package com.fuint.coupon.enums;

/**
 * 活动规则类型枚举
 * <p/>
 * Created by changyouyi on 17/2/15.
 */
public enum ActivityRuleEnum {
    TIME_DISCOUNT("1", "限时折扣"),
    EVERY_MINUS("2", "每满减"),
    FULL_MINUS("3", "满额减"),
    FULL_DISCOUNT("4", "满件折"),
    COUPON("5", "优惠券"),
//    LARGESS("6", "满额赠"),
    RED_PACKAGE("7", "红包");

    private String key;

    private String value;

    ActivityRuleEnum(String key, String value) {
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
