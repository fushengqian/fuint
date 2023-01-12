package com.fuint.common.enums;

/**
 * 充值配置项枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum BalanceSettingEnum {
    RECHARGE_RULE("rechargeRule", "充值规则"),
    RECHARGE_REMARK("rechargeRemark", "充值说明");

    private String key;

    private String value;

    BalanceSettingEnum(String key, String value) {
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
        for (BalanceSettingEnum c : BalanceSettingEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (BalanceSettingEnum c : BalanceSettingEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
