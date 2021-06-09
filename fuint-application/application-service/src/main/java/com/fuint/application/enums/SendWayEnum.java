package com.fuint.application.enums;

/**
 * 卡券发放方式
 * <p/>
 * Created by zach on 2021-03-17.
 */
public enum SendWayEnum {
    BACKEND("backend", "后台发放"),
    OFFLINE("offline", "线下发放"),
    FRONT("front", "前台领取");

    private String key;

    private String value;

    SendWayEnum(String key, String value) {
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
        for (SendWayEnum c : SendWayEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (SendWayEnum c : SendWayEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
