package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 售后订单状态枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum RefundStatusEnum {

    CREATED("A", "待审核"),
    APPROVED("B", "已同意"),
    REJECT("C", "已拒绝"),
    CANCEL("D", "已取消"),
    COMPLETE("E", "已完成");

    private String key;

    private String value;

    RefundStatusEnum(String key, String value) {
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

    public static List<ParamDto> getRefundStatusList() {
        return Arrays.stream(RefundStatusEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
