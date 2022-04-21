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
 * mt_address 实体类
 * Created by FSQ
 * Contact wx fsq_better
 */ 
@Entity 
@Table(name = "mt_address")
public class MtAddress implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 11)
    private Integer id;

   /**
    * 会员ID
    */ 
    @Column(name = "USER_ID", nullable = false, length = 11)
    private Integer userId;

   /**
    * 收货人姓名 
    */ 
    @Column(name = "NAME", nullable = false, length = 30)
    private String name;

   /**
    * 收货手机号 
    */ 
    @Column(name = "MOBILE", length = 20)
    private String mobile;

   /**
    * 省份ID 
    */ 
    @Column(name = "PROVINCE_ID", length = 10)
    private Integer provinceId;

   /**
    * 城市ID 
    */ 
    @Column(name = "CITY_ID", length = 10)
    private Integer cityId;

   /**
    * 区/县ID 
    */ 
    @Column(name = "REGION_ID", length = 11)
    private Integer regionId;

   /**
    * 详细地址 
    */ 
    @Column(name = "DETAIL", length = 255)
    private String detail;

    /**
     * 是否默认
     */
    @Column(name = "IS_DEFAULT", length = 1)
    private String isDefault;

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
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
    this.userId=userId;
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
    public String getIsDefault(){
        return isDefault;
    }
    public void setIsDefault(String isDefault){
        this.isDefault=isDefault;
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

