package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单模式
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum OrderModeEnum {
    EXPRESS("express", "配送"),
    ONESELF("oneself", "自取");

    private String key;

    private String value;

    OrderModeEnum(String key, String value) {
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

    public static List<ParamDto> getOrderModeList() {
        return Arrays.stream(OrderModeEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
