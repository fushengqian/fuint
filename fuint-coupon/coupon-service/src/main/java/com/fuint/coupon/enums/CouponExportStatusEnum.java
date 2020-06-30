package com.fuint.coupon.enums;

/**
 * 优惠券码导出状态枚举
 * Created by zach on 2017/7/25.
 */
public enum CouponExportStatusEnum {
    // 0：未导出；1：已导出
    NOT_EXPORT("0", "未导出"),
    EXPORT("1", "已导出");


    private String key;

    private String value;

    CouponExportStatusEnum(String key, String value) {
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
