package com.fuint.common.dto;

import java.io.Serializable;

/**
 * 商品规格值
 * */
public class GoodsSpecValueDto implements Serializable {

    /**
     * 自增ID
     */
    private Integer specValueId;

    /**
     * 规格名
     */
    private String specName;

    /**
     * 规格值
     */
    private String specValue;

    public Integer getSpecValueId() {
        return specValueId;
    }

    public void setSpecValueId(Integer specValueId) {
        this.specValueId = specValueId;
    }

    public String getSpecValue() {
        return specValue;
    }

    public void setSpecValue(String specValue) {
        this.specValue = specValue;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }
}

