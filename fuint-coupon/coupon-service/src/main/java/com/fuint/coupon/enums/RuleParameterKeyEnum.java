package com.fuint.coupon.enums;

/**
 * 规则项属性key枚举
 * Created by zach on 2017/3/2.
 */
public enum RuleParameterKeyEnum {

    PART("part", "部分"),
    ALL("all", "所有"),
    PLATFORM_ID("platformId", "平台"),
    SHOP_ID("shopId", "渠道"),
    BRAND_CODE("brandCode", "品牌"),
    FIRST_CATEGORY_CODE("firstCategoryCode", "一级分类"),
    SECOND_CATEGORY_CODE("secondCategoryCode", "二级分类"),
    THIRD_CATEGORY_CODE("thirdCategoryCode", "三级分类"),
    PRICE("price", "价格"),
    SKU("sku", "SKU");

    private String code;

    private String value;

    RuleParameterKeyEnum(String code, String value) {
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
