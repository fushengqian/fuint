package com.fuint.common.dto;

import java.io.Serializable;
import java.util.List;

public class GoodsSpecDto implements Serializable {

    /**
     * 自增ID
     */
    private Integer specId;

    /**
     * 规格名称
     */
    private String name;

    /**
     * 规格值
     */
    private List<GoodsSpecValueDto> valueList;

    public Integer getSpecId() {
        return specId;
    }

    public void setSpecId(Integer specId) {
        this.specId = specId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GoodsSpecValueDto> getValueList() {
        return valueList;
    }

    public void setValueList(List<GoodsSpecValueDto> valueList) {
        this.valueList = valueList;
    }
}

