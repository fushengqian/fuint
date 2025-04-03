package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡券类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum CouponTypeEnum {
    COUPON("C", "优惠券"),
    PRESTORE("P", "储值卡"),
    TIMER("T", "计次卡");

    private String key;

    private String value;

    CouponTypeEnum(String key, String value) {
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

    public static List<ParamDto> getCouponTypeList() {
        return Arrays.stream(CouponTypeEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
