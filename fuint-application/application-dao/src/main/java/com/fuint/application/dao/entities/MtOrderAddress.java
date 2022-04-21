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
 * mt_order_address 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */ 
@Entity 
@Table(name = "mt_order_address")
public class MtOrderAddress implements Serializable {
   /**
    * 地址ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 收货人姓名 
    */ 
    @Column(name = "NAME", nullable = false, length = 30)
    private String name;

   /**
    * 联系电话 
    */ 
    @Column(name = "MOBILE", nullable = false, length = 20)
    private String mobile;

   /**
    * 省份ID 
    */ 
    @Column(name = "PROVINCE_ID", nullable = false, length = 10)
    private Integer provinceId;

   /**
    * 城市ID 
    */ 
    @Column(name = "CITY_ID", nullable = false, length = 10)
    private Integer cityId;

   /**
    * 区/县ID 
    */ 
    @Column(name = "REGION_ID", nullable = false, length = 10)
    private Integer regionId;

   /**
    * 详细地址 
    */ 
    @Column(name = "DETAIL", nullable = false, length = 255)
    private String detail;

   /**
    * 订单ID 
    */ 
    @Column(name = "ORDER_ID", nullable = false, length = 10)
    private Integer orderId;

   /**
    * 会员ID
    */ 
    @Column(name = "USER_ID", nullable = false, length = 10)
    private Integer userId;

   /**
    * 创建时间 
    */ 
    @Column(name = "CREATE_TIME", nullable = false)
    private Date createTime;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
    this.name=name;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public Integer getProvinceId(){
        return provinceId;
    }
    public void setProvinceId(Integer provinceId){
    this.provinceId=provinceId;
    }
    public Integer getCityId(){
        return cityId;
    }
    public void setCityId(Integer cityId){
    this.cityId=cityId;
    }
    public Integer getRegionId(){
        return regionId;
    }
    public void setRegionId(Integer regionId){
    this.regionId=regionId;
    }
    public String getDetail(){
        return detail;
    }
    public void setDetail(String detail){
    this.detail=detail;
    }
    public Integer getOrderId(){
        return orderId;
    }
    public void setOrderId(Integer orderId){
    this.orderId=orderId;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
    this.userId=userId;
    }
    public Date getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
    this.createTime=createTime;
    }
}

