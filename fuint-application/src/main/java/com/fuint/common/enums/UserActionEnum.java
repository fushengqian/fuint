package com.fuint.common.enums;

/**
 * 会员行为枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum UserActionEnum {
    REGISTER("register", "注册会员"),
    LOGIN("login", "登录系统"),
    VIEW_GOODS("viewGoods", "浏览商品"),
    SUBMIT_ORDER("submitOrder", "提交订单"),
    CANCEL_ORDER("cancelOrder", "取消订单"),
    GET_COUPON("getCoupon", "领取卡券"),
    USE_COUPON("useCoupon", "使用卡券"),
    RECHARGE_BALANCE("rechargeBalance", "余额充值"),
    USE_BALANCE("useBalance", "使用余额");

    private String key;

    private String value;

    UserActionEnum(String key, String value) {
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
        for (UserActionEnum c : UserActionEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (UserActionEnum c : UserActionEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
