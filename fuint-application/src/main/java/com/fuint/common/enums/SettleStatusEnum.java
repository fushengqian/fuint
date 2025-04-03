package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单结算状态
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum SettleStatusEnum {
    WAIT("A", "待确认"),
    COMPLETE("B", "已完成");

    private String key;

    private String value;

    SettleStatusEnum(String key, String value) {
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

    public static List<ParamDto> getSettleStatusList() {
        return Arrays.stream(SettleStatusEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
