package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分佣对象枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CommissionTargetEnum {
    MEMBER("member", "会员分销"),
    STAFF("staff", "员工提成");

    private String key;

    private String value;

    CommissionTargetEnum(String key, String value) {
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

    public static List<ParamDto> getCommissionTargetList() {
        return Arrays.stream(CommissionTargetEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
