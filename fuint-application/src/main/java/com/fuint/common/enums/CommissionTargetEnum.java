package com.fuint.common.enums;

/**
 * 分佣对象枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CommissionTargetEnum {
    GOODS("goods", "商品销售"),
    SERVICE("service", "服务项目"),
    COUPON("coupon", "卡券销售"),
    RECHARGE("recharge", "会员充值");

    private String key;

    private String value;

    CommissionTargetEnum(String key, String value) {
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
