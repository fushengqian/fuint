package com.fuint.common.enums;

/**
 * 会员来源渠道
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum MemberSourceEnum {
    BACKEND_ADD("backend_add", "后台添加"),
    REGISTER_BY_ACCOUNT("register_by_account", "H5注册"),
    MOBILE_LOGIN("mobile_login", "手机号登录注册"),
    WECHAT_LOGIN("wechat_login", "微信小程序");

    private String key;

    private String value;

    MemberSourceEnum(String key, String value) {
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
        for (MemberSourceEnum c : MemberSourceEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (MemberSourceEnum c : MemberSourceEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
