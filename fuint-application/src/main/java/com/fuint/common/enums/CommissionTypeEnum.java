package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分佣提成类型
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CommissionTypeEnum {
    GOODS("goods", "商品订单"),
    PAYMENT("payment", "付款订单"),
    RECHARGE("recharge", "充值订单");

    private String key;

    private String value;

    CommissionTypeEnum(String key, String value) {
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

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    public static String getName(String key) {
        final CommissionTypeEnum[] values = CommissionTypeEnum.values();
        for (CommissionTypeEnum value : values) {
            if (key.equals(value.getKey())) {
                return value.getValue();
            }
        }
        return null;
    }

    public static List<ParamDto> getCommissionTypeList() {
        return Arrays.stream(CommissionTypeEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
