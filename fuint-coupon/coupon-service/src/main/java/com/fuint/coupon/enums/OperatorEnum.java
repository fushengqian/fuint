package com.fuint.coupon.enums;

/**
 * 运算符枚举类
 * Created by zach on 2017/2/15.
 */
public enum OperatorEnum {
    /**
     * 并且运算符
     */
    AND("&&", "并且"),
    /**
     * 或运算符
     */
    OR("||", "或");

    private String code;

    private String value;

    OperatorEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
