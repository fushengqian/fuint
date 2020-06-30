package com.fuint.coupon.enums;

/**
 * 优惠券内容枚举
 * <p/>
 * Created by zach on 2019/9/6.
 */
public enum CouponContentEnum {

    ROOM("1", "房间"),
    ROOM_BTEAKFAST("2", "房间+早餐"),
    MEALS("3", "餐食"),
    WASH("4", "洗衣"),
    HEALTH("5", "康乐");

    private String key;

    private String value;

    CouponContentEnum(String key, String value) {
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
