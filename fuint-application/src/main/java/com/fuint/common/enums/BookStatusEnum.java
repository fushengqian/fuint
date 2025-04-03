package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预约订单状态枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum BookStatusEnum {
    CREATED("A", "待审核"),
    CONFIRM("B", "审核通过"),
    FAIL("F", "审核未通过"),
    CANCEL("C", "已取消"),
    DELETE("D", "已删除"),
    COMPLETE("E", "已完成");

    private String key;

    private String value;

    BookStatusEnum(String key, String value) {
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

    public static List<ParamDto> getBookStatusList() {
        return Arrays.stream(BookStatusEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
