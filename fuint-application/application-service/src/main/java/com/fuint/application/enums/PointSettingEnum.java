package com.fuint.application.enums;

/**
 * 积分配置项枚举
 *
 * Created by zach on 2021/05/16.
 */
public enum PointSettingEnum {
    POINT_NEED_CONSUME("pointNeedConsume", "返1积分所需消费金额"),
    CAN_USE_AS_MONEY("canUsedAsMoney", "是否可当作现金使用"),
    EXCHANGE_NEED_POINT("exchangeNeedPoint", "多少积分可抵扣1元现金"),
    RECHARGE_POINT_SPEED("rechargePointSpeed", "充值返积分倍数");

    private String key;

    private String value;

    PointSettingEnum(String key, String value) {
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
        for (SendWayEnum c : SendWayEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (SendWayEnum c : SendWayEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
