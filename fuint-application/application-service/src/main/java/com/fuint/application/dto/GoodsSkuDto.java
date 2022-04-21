package com.fuint.application.dto;

import java.math.BigDecimal;

public class GoodsSkuDto {
    /**
     * 自增ID
     */
    private Integer id;

    /**
     * sku编码
     */
    private String skuNo;

    /**
     * 图片
     */
    private String logo;

    /**
     * 商品ID
     */
    private Integer goodsId;

    /**
     * 规格ID
     */
    private String specIds;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 划线价格
     */
    private BigDecimal linePrice;

    /**
     * 重量
     */
    private BigDecimal weight;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public String getSkuNo(){
        return skuNo;
    }
    public void setSkuNo(String skuNo){
        this.skuNo=skuNo;
    }
    public String getLogo(){
        return logo;
    }
    public void setLogo(String logo){
        this.logo=logo;
    }
    public Integer getGoodsId(){
        return goodsId;
    }
    public void setGoodsId(Integer goodsId){
        this.goodsId=goodsId;
    }
    public String getSpecIds(){
        return specIds;
    }
    public void setSpecIds(String specIds){
        this.specIds=specIds;
    }
    public Integer getStock(){
        return stock;
    }
    public void setStock(Integer stock){
        this.stock=stock;
    }
    public BigDecimal getPrice(){
        return price;
    }
    public void setPrice(BigDecimal price){
        this.price=price;
    }
    public BigDecimal getLinePrice(){
        return linePrice;
    }
    public void setLinePrice(BigDecimal linePrice){
        this.linePrice=linePrice;
    }
    public BigDecimal getWeight(){
        return weight;
    }
    public void setWeight(BigDecimal weight){
        this.weight=weight;
    }
}
