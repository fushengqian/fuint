package com.fuint.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 订单实体类
 * */
public class OrderGoodsDto implements Serializable {
    private Integer id;

    private Integer goodsId;

    private String type;

    private String name;

    private String price;

    private String discount;

    private Integer num;

    private String image;

    private Integer skuId;

    private List<GoodsSpecValueDto> specList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public List<GoodsSpecValueDto> getSpecList() {
        return specList;
    }

    public void setSpecList(List<GoodsSpecValueDto> specList) {
        this.specList = specList;
    }
}

