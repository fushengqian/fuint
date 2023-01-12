package com.fuint.common.enums;

/**
 * 用户卡券状态
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum UserCouponStatusEnum {
    UNUSED("A", "未使用"),
    USED("B", "已使用"),
    EXPIRE("C", "已过期"),
    DISABLE("D", "不可用"),
    UNSEND("E", "待领取");

    private String key;

    private String value;

    UserCouponStatusEnum(String key, String value) {
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

    // 普通方法，通过key获取value
    public static String getValue(String k) {
        for (UserCouponStatusEnum c : UserCouponStatusEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (UserCouponStatusEnum c : UserCouponStatusEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
