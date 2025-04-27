package com.fuint.common.enums;

/**
 * 二维码枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum QrCodeEnum {
    TABLE("table", "桌码二维码", "pages/category/index"),
    STORE("store", "店铺二维码", "pages/index/index"),
    COUPON("coupon", "卡券二维码", "pages/coupon/detail");

    private String key;

    private String value;

    private String page;

    QrCodeEnum(String key, String value, String page) {
        this.key = key;
        this.value = value;
        this.page = page;
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

    public String getPage() {
        return page;
    }
    public void setPage(String page) {
        this.page = page;
    }

    // 普通方法，通过key获取value
    public static String getValue(String k) {
        for (QrCodeEnum c : QrCodeEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (QrCodeEnum c : QrCodeEnum.values()) {
            if (c.getValue().equals(v)) {
                return c.getKey();
            }
        }
        return null;
    }

    // 普通方法，通过key获取page
    public static String getPage(String k) {
        for (QrCodeEnum c : QrCodeEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getPage();
            }
        }
        return null;
    }
}
