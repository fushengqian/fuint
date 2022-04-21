package com.fuint.application.enums;

/**
 * 卡券适用商品
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public enum ApplyGoodsEnum {
    BACKEND("allGoods", "全场通用"),
    FRONT("parkGoods", "指定商品");

    private String key;

    private String value;

    ApplyGoodsEnum(String key, String value) {
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
