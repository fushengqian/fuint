package com.fuint.coupon.enums;

/**
 * 参数类型枚举类
 * Created by zach on 2017/2/15.
 */
public enum ParameterTypeEnum {
    /**
     * 标准参数
     */
    NORMAL("0", "标准参数"),
    /**
     * 自定义参数
     */
    DEFINED("1", "自定义参数");

    private String code;

    private String value;

    ParameterTypeEnum(String code, String value) {
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
