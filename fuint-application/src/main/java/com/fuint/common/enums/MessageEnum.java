package com.fuint.common.enums;

/**
 * 会员消息类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum MessageEnum {
    POP_MSG("pop", "弹框消息"),
    SUB_MSG("sub", "订阅消息"),
    SMS_MSG("sms", "短信消息");

    private String key;
    private String value;

    MessageEnum(String key, String value) {
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
