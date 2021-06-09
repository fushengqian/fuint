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
 * mt_give 实体类
 * Created by zach
 * Sat Oct 12 15:34:19 GMT+08:00 2019
 */
@Entity 
@Table(name = "mt_give")
public class MtGive implements Serializable{
   /**
    * 自增 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 获赠者用户ID 
    */ 
    @Column(name = "USER_ID", nullable = false, length = 10)
    private Integer userId;

   /**
    * 赠送者用户ID 
    */ 
    @Column(name = "GIVE_USER_ID", nullable = false, length = 10)
    private Integer giveUserId;

   /**
    * 赠予对象手机号 
    */ 
    @Column(name = "MOBILE", nullable = false, length = 20)
    private String mobile;

   /**
    * 用户手机 
    */ 
    @Column(name = "USER_MOBILE", nullable = false, length = 20)
    private String userMobile;

   /**
    * 分组ID，逗号隔开
    */ 
    @Column(name = "GROUP_IDS", nullable = false, length = 200)
    private String groupIds;

   /**
    * 分组名称，逗号隔开
    */ 
    @Column(name = "GROUP_NAMES", nullable = false, length = 500)
    private String groupNames;

   /**
    * 券ID，逗号隔开 
    */ 
    @Column(name = "COUPON_IDS", nullable = false, length = 200)
    private String couponIds;

   /**
    * 券名称，逗号隔开 
    */ 
    @Column(name = "COUPON_NAMES", nullable = false, length = 500)
    private String couponNames;

   /**
    * 数量 
    */ 
    @Column(name = "NUM", nullable = false, length = 10)
    private Integer num;

   /**
    * 总金额 
    */ 
    @Column(name = "MONEY", nullable = false)
    private BigDecimal money;

   /**
    * 备注 
    */ 
    @Column(name = "NOTE", length = 200)
    private String note;

   /**
    * 留言
    */
   @Column(name = "MESSAGE", length = 500)
   private String message;

   /**
    * 赠送时间 
    */ 
    @Column(name = "CREATE_TIME", nullable = false)
    private Date createTime;

   /**
    * 更新时间 
    */ 
    @Column(name = "UPDATE_TIME", nullable = false)
    private Date updateTime;

   /**
    * 状态，A正常；C取消 
    */ 
    @Column(name = "STATUS", nullable = false, length = 1)
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
    public Integer getGiveUserId(){
        return giveUserId;
    }
    public void setGiveUserId(Integer giveUserId){
    this.giveUserId=giveUserId;
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
    public String getGroupIds(){
        return groupIds;
    }
    public void setGroupIds(String groupIds){
    this.groupIds=groupIds;
    }
    public String getGroupNames(){
        return groupNames;
    }
    public void setGroupNames(String groupNames){
    this.groupNames=groupNames;
    }
    public String getCouponIds(){
        return couponIds;
    }
    public void setCouponIds(String couponIds){
    this.couponIds=couponIds;
    }
    public String getCouponNames(){
        return couponNames;
    }
    public void setCouponNames(String couponNames){
    this.couponNames=couponNames;
    }
    public Integer getNum(){
        return num;
    }
    public void setNum(Integer num){
    this.num=num;
    }
    public BigDecimal getMoney(){
        return money;
    }
    public void setMoney(BigDecimal money){
    this.money=money;
    }
    public String getNote(){
        return note;
    }
    public void setNote(String note){
    this.note=note;
    }
    public String getMessage(){
           return message;
    }
    public void setMessage(String message){ this.message=message; }
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

