package com.fuint.common.enums;

/**
 * 分佣提现付款方式
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CommissionPayTypeEnum {

    BALANCE("A", "付款到会员余额"),

    WECHAT("B", "付款到微信钱包");

    private String key;

    private String value;

    CommissionPayTypeEnum(String key, String value) {
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
