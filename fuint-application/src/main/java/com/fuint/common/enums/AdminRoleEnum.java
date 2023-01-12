package com.fuint.common.enums;

/**
 * 后台角色枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum AdminRoleEnum {
    ADMIN("1", "超级管理员","admin"),
    COMMON("2", "普通管理员","common"),
    USER("3", "用户角色","user");

    private String key;
    private String name;
    private String value;

    AdminRoleEnum(String key, String name, String value) {
        this.key = key;
        this.name = name;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // 普通方法，通过key获取value
    public static String getValue(String k) {
        for (AdminRoleEnum c : AdminRoleEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过key获取name
    public static String getName(String k) {
        for (AdminRoleEnum c : AdminRoleEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getName();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (AdminRoleEnum c : AdminRoleEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
