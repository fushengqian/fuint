package com.fuint.common.enums;

/**
 * 微信订阅消息枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum WxMessageEnum {
    ORDER_CREATED("orderCreated", "订单生成提醒"),
    DELIVER_GOODS("deliverGoods", "订单发货提醒"),
    COUPON_EXPIRE("couponExpire", "卡券到期提醒"),
    COUPON_ARRIVAL("couponArrival", "卡券到账提醒"),
    BALANCE_CHANGE("balanceChange", "余额变动提醒"),
    COUPON_CONFIRM("couponConfirm", "卡券核销提醒"),
    POINT_CHANGE("pointChange", "积分变更提醒");

    private String key;

    private String value;

    WxMessageEnum(String key, String value) {
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
        for (WxMessageEnum c : WxMessageEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (WxMessageEnum c : WxMessageEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
