package com.fuint.common.enums;

/**
 * 卡券内容枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
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
