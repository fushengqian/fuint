package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户类型枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum MerchantTypeEnum {
    RESTAURANT("restaurant", "餐饮：餐厅、奶茶、酒店等"),
    RETAIL("retail", "零售：超市、生鲜、卖场等"),
    SERVICE("service", "服务：美容、足浴、汽车4s店等"),
    OTHER("other", "其他");

    private String key;

    private String value;

    MerchantTypeEnum(String key, String value) {
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

    public static List<ParamDto> getMerchantTypeList() {
        return Arrays.stream(MerchantTypeEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
