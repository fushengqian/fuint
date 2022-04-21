package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * mt_give 实体类
 * Created by FSQ
 * Contact wx fsq_better
 */
public class GiveItemDto implements Serializable{
   /**
    * 自增 
    */
    private Integer id;

   /**
    * 赠予对象手机号 
    */
    private String mobile;

   /**
    * 用户手机 
    */
    private String userMobile;

   /**
    * 分组ID
    */
    private Integer groupId;

   /**
    * 分组名称
    */
    private String groupName;

   /**
    * 券ID
    */
    private Integer couponId;

   /**
    * 券名称
    */
    private String couponName;

   /**
    * 总金额 
    */
    private BigDecimal money;

   /**
    * 赠送时间 
    */
    private Date createTime;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public String getUserMobile(){
        return userMobile;
    }
    public void setUserMobile(String userMobile){
    this.userMobile=userMobile;
    }
    public Integer getGroupId(){
        return groupId;
    }
    public void setGroupId(Integer groupId){
    this.groupId=groupId;
    }
    public String getGroupName(){
        return groupName;
    }
    public void setGroupName(String groupName){
    this.groupName=groupName;
    }
    public Integer getCouponId(){
        return couponId;
    }
    public void setCouponId(Integer couponId){
    this.couponId=couponId;
    }
    public String getCouponName(){
        return couponName;
    }
    public void setCouponName(String couponName){
    this.couponName=couponName;
    }
    public BigDecimal getMoney(){
        return money;
    }
    public void setMoney(BigDecimal money){
    this.money=money;
    }
    public Date getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
    this.createTime=createTime;
    }
}
