package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 售后类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum RefundTypeEnum {

    RETURN("return", "退货退款"),
    EXCHANGE("exchange", "换货");

    private String key;

    private String value;

    RefundTypeEnum(String key, String value) {
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

    public static List<ParamDto> getRefundTypeList() {
        return Arrays.stream(RefundTypeEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
