package com.fuint.common.enums;

/**
 * 卡券内容枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CouponContentEnum {
    AMOUNT("1", "满减券"),
    PERCENT("2", "折扣券");

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
