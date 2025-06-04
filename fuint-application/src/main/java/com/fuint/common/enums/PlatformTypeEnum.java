package com.fuint.common.enums;

/**
 * 平台类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum PlatformTypeEnum {

    /**
     * 微信小程序
     */
    MP_WEIXIN("MP-WEIXIN", "微信小程序", 1),

    /**
     * PC
     */
    PC("PC", "PC端", 2),

    /**
     * H5
     */
    H5("H5", "H5端", 3),

    /**
     * App客户端
     */
    APP("APP", "App客户端", 4);

    private String code;

    private String value;

    private Integer num;

    PlatformTypeEnum(String code, String value, Integer num) {
        this.code = code;
        this.value = value;
        this.num = num;
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
