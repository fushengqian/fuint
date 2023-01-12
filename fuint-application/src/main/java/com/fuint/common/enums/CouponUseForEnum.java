package com.fuint.common.enums;

/**
 * 卡券使用专项枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CouponUseForEnum {
    MEMBER_GRADE("memberGrade", "升级会员等级专用"),
    OFF_LINE_PAYMENT("offLinePayment", "到店买单支付专用");

    private String key;

    private String value;

    CouponUseForEnum(String key, String value) {
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
        for (CouponUseForEnum c : CouponUseForEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (CouponUseForEnum c : CouponUseForEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
