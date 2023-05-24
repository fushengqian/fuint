package com.fuint.common.enums;

/**
 * 交易配置项枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum OrderSettingEnum {
    DELIVERY_FEE("deliveryFee", "订单配送费用"),
    IS_CLOSE("isClose", "关闭交易功能");

    private String key;

    private String value;

    OrderSettingEnum(String key, String value) {
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
        for (OrderSettingEnum c : OrderSettingEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (OrderSettingEnum c : OrderSettingEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
