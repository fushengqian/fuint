package com.fuint.common.enums;

/**
 * 标签规则类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum TagRuleTypeEnum {
    CONSUME_COUNT("consume_count", "消费次数"),
    CONSUME_AMOUNT("consume_amount", "消费金额"),
    LAST_CONSUME("last_consume", "最后消费时间"),
    REGISTER_TIME("register_time", "注册时间"),
    SINGLE_ORDER_AMOUNT("single_order_amount", "单笔订单金额"),
    AVG_ORDER_AMOUNT("avg_order_amount", "平均订单金额"),
    TOTAL_ORDER_COUNT("total_order_count", "累计订单数"),
    POINT_BALANCE("point_balance", "积分余额");

    private String key;
    private String value;

    TagRuleTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
