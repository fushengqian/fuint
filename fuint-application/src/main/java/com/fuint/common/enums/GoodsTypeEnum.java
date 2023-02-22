package com.fuint.common.enums;

/**
 * 商品类型
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum GoodsTypeEnum {
    PRODUCT("product", "实物商品"),
    SERVICE("service", "服务项目"),
    COUPON("coupon", "虚拟卡券");

    private String key;

    private String value;

    GoodsTypeEnum(String key, String value) {
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
        for (GoodsTypeEnum c : GoodsTypeEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (GoodsTypeEnum c : GoodsTypeEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
