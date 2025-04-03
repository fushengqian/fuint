package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付状态
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum PayStatusEnum {
    WAIT("A", "待支付"),
    SUCCESS("B", "已支付");

    private String key;

    private String value;

    PayStatusEnum(String key, String value) {
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

    public static List<ParamDto> getPayStatusList() {
        return Arrays.stream(PayStatusEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
