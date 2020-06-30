package com.fuint.coupon.enums;

/**
 * 规则Code类型枚举
 * Created by zach on 2017/2/14.
 */
public enum CodeTypeEnum {

    /**
     * 促销范围规则Code类型
     */
    JOIN_RANGE_RULE("JRR", "促销范围规则"),
    /**
     * 促销特例规则Code类型
     */
    SPECIAL_CASE_RULE("SCR", "促销特例规则"),
    /**
     * 促销活动规则Code类型
     */
    ACTIVITY_RULE("AR", "促销活动规则"),
    /**
     * 促销活动信息Code类型
     */
    ACTIVITY_INFO("AI", "促销活动信息"),
    /**
     * 特权码活动信息Code类型
     */
    PRIVILEGE_ACTIVITY_INFO("PAI", "特权码活动信息"),

    /**
     * 特权码推广规则Code类型
     */
    PRIVILEGE_EXTEND("PE", "特权码推广规则"),

    /**
     * 特权码规则Code类型
     */
    PRIVILEGE_RULE("PR", "特权码规则"),

    /**
     * 优惠券促销范围规则类型
     */
    PRODUCT_RANGE_RULE("CRR", "优惠券促销范围规则"),

    /**
     * 优惠券剔除范围规则
     */
    PRODUCT_SPECIAL_RULE("CSR", "优惠券剔除范围规则"),

    /***
     * 优惠券推广用户范围
     */
    COUPON_EXTEND_USER("CEU","优惠券推广用户范围"),

    /***
     * 优惠券推广规则
     */
    COUPON_EXTEND_RULE("CER","优惠券推广规则"),

    /**
     * 优惠券活动组Code类型
     */
    COUPON_ACTIVITY_GROUP("CAG", "优惠券活动组"),

    /**
     * 适用用户范围
     */
    USER_RULE("UR", "用户范围规则"),

    /**
     * 优惠券活动Code类型
     */
    COUPON_ACTIVITY_INFO("CAI","优惠券活动"),


    /**
     * 优惠券链接模板
     */
    COUPON_LINK_TEMPLATE("CLT", "优惠券链接模板"),

    /**
     * 优惠券链接活动
     */
    COUPON_LINK_ACTIVITY("CLA", "优惠券链接活动"),
    /**
     * 用户范围规则
     */
    USER_RANGE_RULE("UR", "用户范围规则");

    private String code;

    private String value;

    CodeTypeEnum(String code, String value) {
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
