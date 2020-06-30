package com.fuint.coupon.enums;

/**
 * 平台类型枚举
 * Created by zach on 2017/8/22.
 */
public enum PlatformTypeEnum {

    /**
     * PC
     */
    PC("P", "并且"),

    /**
     * 移动端
     */
    MOBILE("M", "或");

    private String code;

    private String value;

    PlatformTypeEnum(String code, String value) {
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
