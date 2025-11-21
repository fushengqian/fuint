package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 开票状态枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum InvoiceStatusEnum {

    CREATED("A", "开票中"),
    SUCCESS("B", "开票成功"),
    CANCEL("C", "已冲红");

    private String key;

    private String value;

    InvoiceStatusEnum(String key, String value) {
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
        final InvoiceStatusEnum[] values = InvoiceStatusEnum.values();
        for (InvoiceStatusEnum value : values) {
            if (key.equals(value.getKey())) {
                return value.getValue();
            }
        }
        return null;
    }

    public static List<ParamDto> getInvoiceStatusList(String... excludedKeys) {
        List<String> excludedKeySet = Arrays.asList(excludedKeys);
        return Arrays.stream(InvoiceStatusEnum.values())
                .filter(status -> !excludedKeySet.contains(status.getKey()))
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
