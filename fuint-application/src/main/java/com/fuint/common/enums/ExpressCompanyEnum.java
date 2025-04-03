package com.fuint.common.enums;

import com.fuint.common.dto.ParamDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物流公司枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum ExpressCompanyEnum {

    SELF("SELF", "商家自送"),
    YTO("YTO", "圆通速递"),
    ZTO("ZTO", "中通快递"),
    BEST("BEST", "百世快递"),
    YUNDA("YUNDA", "韵达快递"),
    SF("SF", "顺丰速运"),
    EMS("EMS", "中国邮政"),
    DB("DB", "德邦快递"),
    STO("STO", "申通快递"),
    JDL("JDL", "京东快递"),
    HHTT("HHTT", "天天快递"),
    JTSD("JTSD", "极兔快递");

    private String key;

    private String value;

    ExpressCompanyEnum(String key, String value) {
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

    public static List<ParamDto> getExpressCompanyList() {
        return Arrays.stream(ExpressCompanyEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
