package com.fuint.common.enums;

/**
 * 卡券内容枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CouponContentEnum {
    AMOUNT(1, "满减券"),
    PERCENT(2, "折扣券");

    private Integer key;

    private String value;

    CouponContentEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
