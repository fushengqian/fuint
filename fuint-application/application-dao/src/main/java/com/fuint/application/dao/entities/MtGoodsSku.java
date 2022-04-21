package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * mt_goods_sku 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */ 
@Entity 
@Table(name = "mt_goods_sku")
public class MtGoodsSku implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * sku编码 
    */ 
    @Column(name = "SKU_NO", length = 50)
    private String skuNo;

   /**
    * 图片 
    */ 
    @Column(name = "LOGO", length = 255)
    private String logo;

   /**
    * 商品ID 
    */ 
    @Column(name = "GOODS_ID", nullable = false, length = 10)
    private Integer goodsId;

   /**
    * 规格ID 
    */ 
    @Column(name = "SPEC_IDS", nullable = false, length = 100)
    private String specIds;

   /**
    * 库存 
    */ 
    @Column(name = "STOCK", nullable = false, length = 10)
    private Integer stock;

   /**
    * 价格 
    */ 
    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;

   /**
    * 划线价格 
    */ 
    @Column(name = "LINE_PRICE", nullable = false)
    private BigDecimal linePrice;

   /**
    * 重量 
    */ 
    @Column(name = "WEIGHT")
    private BigDecimal weight;

    /**
     * 状态
     */
    @Column(name = "STATUS", length = 1)
    private String status;

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
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
}

