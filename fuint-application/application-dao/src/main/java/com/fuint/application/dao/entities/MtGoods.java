package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * mt_goods 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */ 
@Entity 
@Table(name = "mt_goods")
public class MtGoods implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 11)
    private Integer id;

    /**
     * 店铺ID
     */
    @Column(name = "STORE_ID")
    private Integer storeId;

   /**
    * 商品名称 
    */ 
    @Column(name = "NAME", length = 100)
    private String name;

   /**
    * 分类ID 
    */ 
    @Column(name = "CATE_ID", length = 10)
    private Integer cateId;

   /**
    * 商品条码
    */ 
    @Column(name = "GOODS_NO", length = 100)
    private String goodsNo;

    /**
     * 可否单规格
     */
    @Column(name = "IS_SINGLE_SPEC", length = 1)
    private String isSingleSpec;

   /**
    * 主图地址 
    */ 
    @Column(name = "LOGO", length = 200)
    private String logo;

   /**
    * 图片地址 
    */ 
    @Column(name = "IMAGES", length = 1000)
    private String images;

   /**
    * 价格 
    */ 
    @Column(name = "PRICE")
    private BigDecimal price;

   /**
    * 划线价格 
    */ 
    @Column(name = "LINE_PRICE")
    private BigDecimal linePrice;

   /**
    * 库存 
    */ 
    @Column(name = "STOCK", length = 10)
    private Integer stock;

   /**
    * 重量 
    */ 
    @Column(name = "WEIGHT")
    private BigDecimal weight;

   /**
    * 初始销量 
    */ 
    @Column(name = "INIT_SALE", length = 10)
    private Integer initSale;

   /**
    * 商品卖点 
    */ 
    @Column(name = "SALE_POINT", length = 100)
    private String salePoint;

   /**
    * 可否使用积分抵扣 
    */ 
    @Column(name = "CAN_USE_POINT", length = 1)
    private String canUsePoint;

   /**
    * 会员是否有折扣 
    */ 
    @Column(name = "IS_MEMBER_DISCOUNT", length = 1)
    private String isMemberDiscount;

   /**
    * 排序 
    */ 
    @Column(name = "SORT", length = 10)
    private Integer sort;

   /**
    * 商品描述 
    */ 
    @Column(name = "DESCRIPTION")
    private String description;

   /**
    * 创建时间 
    */ 
    @Column(name = "CREATE_TIME")
    private Date createTime;

   /**
    * 更新时间 
    */ 
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

   /**
    * 最后操作人 
    */ 
    @Column(name = "OPERATOR", length = 30)
    private String operator;

   /**
    * A：正常；D：删除 
    */ 
    @Column(name = "STATUS", length = 1)
    private String status;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
        this.storeId=storeId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public Integer getCateId(){
        return cateId;
    }
    public void setCateId(Integer cateId){
        this.cateId=cateId;
    }
    public String getGoodsNo(){
        return goodsNo;
    }
    public void setGoodsNo(String goodsNo){
        this.goodsNo=goodsNo;
    }
    public String getIsSingleSpec(){
        return isSingleSpec;
    }
    public void setIsSingleSpec(String isSingleSpec){
        this.isSingleSpec=isSingleSpec;
    }
    public String getLogo(){
        return logo;
    }
    public void setLogo(String logo){
        this.logo=logo;
    }
    public String getImages(){
        return images;
    }
    public void setImages(String images){
        this.images=images;
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
    public Integer getStock(){
        return stock;
    }
    public void setStock(Integer stock){
        this.stock=stock;
    }
    public BigDecimal getWeight(){
        return weight;
    }
    public void setWeight(BigDecimal weight){
        this.weight=weight;
    }
    public Integer getInitSale(){
        return initSale;
    }
    public void setInitSale(Integer initSale){
        this.initSale=initSale;
    }
    public String getSalePoint(){
        return salePoint;
    }
    public void setSalePoint(String salePoint){
        this.salePoint=salePoint;
    }
    public String getCanUsePoint(){
        return canUsePoint;
    }
    public void setCanUsePoint(String canUsePoint){
        this.canUsePoint=canUsePoint;
    }
    public String getIsMemberDiscount(){
        return isMemberDiscount;
    }
    public void setIsMemberDiscount(String isMemberDiscount){
        this.isMemberDiscount=isMemberDiscount;
    }
    public Integer getSort(){
        return sort;
    }
    public void setSort(Integer sort){
        this.sort=sort;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public Date getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime=createTime;
    }
    public Date getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(Date updateTime){
        this.updateTime=updateTime;
    }
    public String getOperator(){
        return operator;
    }
    public void setOperator(String operator){
        this.operator=operator;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
}

