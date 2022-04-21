package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

/**
 * mt_coupon_goods 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */ 
@Entity 
@Table(name = "mt_coupon_goods")
public class MtCouponGoods implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 11)
    private Integer id;

   /**
    * 卡券ID 
    */ 
    @Column(name = "COUPON_ID", nullable = false, length = 10)
    private Integer couponId;

   /**
    * 商品ID 
    */ 
    @Column(name = "GOODS_ID", nullable = false, length = 10)
    private Integer goodsId;

   /**
    * 创建时间 
    */ 
    @Column(name = "CREATE_TIME", nullable = false)
    private Date createTime;

   /**
    * 更新时间 
    */ 
    @Column(name = "UPDATE_TIME", nullable = false)
    private Date updateTime;

   /**
    * 状态 
    */ 
    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public Integer getCouponId(){
        return couponId;
    }
    public void setCouponId(Integer couponId){
        this.couponId=couponId;
    }
    public Integer getGoodsId(){
        return goodsId;
    }
    public void setGoodsId(Integer goodsId){
        this.goodsId=goodsId;
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
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
}

