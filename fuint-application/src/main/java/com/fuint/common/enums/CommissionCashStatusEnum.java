package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分佣结算状态
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CommissionCashStatusEnum {
    WAIT("A", "待确认"),
    SETTLED("B", "已确认"),
    CANCEL("C", "已打款"),
    PAYED("D", "已作废");

    private String key;

    private String value;

    CommissionCashStatusEnum(String key, String value) {
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

    public static List<ParamDto> getCommissionCashStatusList() {
        return Arrays.stream(CommissionCashStatusEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
