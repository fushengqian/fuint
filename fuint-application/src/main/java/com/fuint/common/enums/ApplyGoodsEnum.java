package com.fuint.common.enums;

/**
 * 卡券适用商品
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum ApplyGoodsEnum {
    ALL_GOODS("allGoods", "全场通用"),
    PARK_GOODS("parkGoods", "指定商品");

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
        for (ApplyGoodsEnum c : ApplyGoodsEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (ApplyGoodsEnum c : ApplyGoodsEnum.values()) {
            if (c.getValue().equals(v)) {
                return c.getKey();
            }
        }
        return null;
    }
}
