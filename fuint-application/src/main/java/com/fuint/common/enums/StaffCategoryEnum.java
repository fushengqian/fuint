package com.fuint.common.enums;

/**
 * 员工类别枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum StaffCategoryEnum {
    MANAGER("1", "店长","admin"),
    CASHIER("2", "收银人员","cashier"),
    SALE("3", "销售人员","sale"),
    SERVICE("4", "服务人员","service");

    private String key;
    private String name;
    private String value;

    StaffCategoryEnum(String key, String name, String value) {
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
        for (StaffCategoryEnum c : StaffCategoryEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    // 普通方法，通过key获取name
    public static String getName(String k) {
        for (StaffCategoryEnum c : StaffCategoryEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getName();
            }
        }
        return null;
    }

    // 普通方法，通过Value获取key
    public static String getKey(String v) {
        for (StaffCategoryEnum c : StaffCategoryEnum.values()) {
            if (c.getValue() == v) {
                return c.getKey();
            }
        }
        return null;
    }
}
