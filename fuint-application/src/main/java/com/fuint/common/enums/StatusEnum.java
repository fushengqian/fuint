package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用状态枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum StatusEnum {

    ENABLED("A", "启用"),
    EXPIRED("C", "过期"),
    DISABLE("D", "删除"),
    FORBIDDEN("N", "禁用"),
    UnAudited("U", "待审核");

    private String key;

    private String value;

    StatusEnum(String key, String value) {
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

    public static List<ParamDto> getStatusList() {
        return Arrays.stream(StatusEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
