package com.fuint.common.enums;

/**
 * 卡券过期类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CouponExpireTypeEnum {
    FIX("fix", "固定期限"),
    FLEX("flex", "领取后生效");

    private String key;

    private String value;

    CouponExpireTypeEnum(String key, String value) {
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
