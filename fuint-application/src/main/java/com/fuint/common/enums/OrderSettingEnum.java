package com.fuint.common.enums;

/**
 * 交易配置项枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum OrderSettingEnum {
    DELIVERY_FEE("deliveryFee", "订单配送费用"),
    DELIVERY_MIN_AMOUNT("deliveryMinAmount", "订单起送金额"),
    IS_CLOSE("isClose", "关闭交易功能"),
    MP_UPLOAD_SHIPPING("mpUploadShipping", "微信小程序上传发货信息"),
    PAY_OFF_LINE("payOffLine", "开启前台支付功能");

    private String key;

    private String value;

    OrderSettingEnum(String key, String value) {
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
        for (OrderSettingEnum c : OrderSettingEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (OrderSettingEnum c : OrderSettingEnum.values()) {
            if (c.getValue().equals(v)) {
                return c.getKey();
            }
        }
        return null;
    }
}
