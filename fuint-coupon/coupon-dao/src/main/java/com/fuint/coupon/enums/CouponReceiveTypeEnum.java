package com.fuint.coupon.enums;

/**
 * 优惠券码获取来源类型枚举
 * Created by zach on 2017/7/25.
 */
public enum CouponReceiveTypeEnum {
    // 0：用户领取；1：系统发放；2：人工发放；3：口令兑换；4：秒杀支付
    USER_RECEIVE("0", "用户领取"),
    SYSTEM_RELEASE("1", "系统发放"),
    MANAGER_RELEASE("2", "人工发放"),
    EXCHANGE("3", "口令兑换"),
    SECKILL_PAY("4", "秒杀支付");


    private String key;

    private String value;

    CouponReceiveTypeEnum(String key, String value) {
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
