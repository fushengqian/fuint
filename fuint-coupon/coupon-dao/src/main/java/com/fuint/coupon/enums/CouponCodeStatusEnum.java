package com.fuint.coupon.enums;

/**
 * 优惠券码状态枚举
 * Created by zach on 2017/7/25.
 */
public enum CouponCodeStatusEnum {
    // 0:未领取； 1：已领取未使用；2：已冻结；3：已使用；4：已结算；9：已作废 N:已过期
    NOT_RECEIVE("0", "未领取"),
    NOT_USED("1", "已领取未使用"),
    FREEZE("2", "已冻结"),
    USED("3", "已使用"),
    SETTLED("4", "已结算"),
    DELETE("9", "已作废"),
    OVERDUE("N", "已过期");

    private String key;

    private String value;

    CouponCodeStatusEnum(String key, String value) {
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
