package com.fuint.common.enums;

import com.fuint.common.dto.common.ParamDto;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评价审核状态枚举
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public enum AuditStatusEnum {

    WAIT("A", "待审核"),
    PASS("P", "审核通过"),
    REJECT("R", "审核拒绝");

    private String key;

    private String value;

    AuditStatusEnum(String key, String value) {
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

    public static String getValue(String k) {
        for (AuditStatusEnum c : AuditStatusEnum.values()) {
            if (c.getKey().equals(k)) {
                return c.getValue();
            }
        }
        return null;
    }

    public static List<ParamDto> getAuditStatusList() {
        return Arrays.stream(AuditStatusEnum.values())
                .map(status -> new ParamDto(status.getKey(), status.getValue(), status.getValue()))
                .collect(Collectors.toList());
    }
}
