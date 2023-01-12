package com.fuint.common.dto;

import java.io.Serializable;
import java.util.List;

public class GoodsSpecItemDto implements Serializable {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 规格名称
     */
    private String name;

    /**
     * 规格值
     */
    private List<GoodsSpecChildDto> child;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GoodsSpecChildDto> getChild() {
        return child;
    }

    public void setChild(List<GoodsSpecChildDto> child) {
        this.child = child;
    }
}

