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
    CREATED("A", "待确认"),
    CONFIRM("B", "确认通过"),
    FAIL("F", "预约失败"),
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

    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        final BookStatusEnum[] values = BookStatusEnum.values();
        for (BookStatusEnum value : values) {
            if (key.equals(value.getKey())) {
                return value.getValue();
            }
        }
        return null;
    }

    public static List<ParamDto> getBookStatusList(String... excludedKeys) {
        List<String> excludedKeySet = Arrays.asList(excludedKeys);
        return Arrays.stream(BookStatusEnum.values())
                .filter(status -> !excludedKeySet.contains(status.getKey()))
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
