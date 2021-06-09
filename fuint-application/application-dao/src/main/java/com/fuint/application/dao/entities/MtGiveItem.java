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
    * mt_give_item 实体类
    * Created by zach
    * Wed Oct 09 10:08:14 GMT+08:00 2019
    */ 
@Entity 
@Table(name = "mt_give_item")
public class MtGiveItem implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 转赠ID 
    */ 
    @Column(name = "GIVE_ID", nullable = false, length = 10)
    private Integer giveId;

   /**
    * 用户卡券ID
    */ 
    @Column(name = "USER_COUPON_ID", nullable = false, length = 10)
    private Integer userCouponId;

   /**
    * 创建时间 
    */ 
    @Column(name = "CREATE_TIME", nullable = false)
    private Date createTime;

   /**
    * 更新时间 
    */ 
    @Column(name = "UPDATE_TIEM", nullable = false)
    private Date updateTiem;

   /**
    * 状态，A正常；D删除 
    */ 
    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public Integer getGiveId(){
        return giveId;
    }
    public void setGiveId(Integer giveId){
    this.giveId=giveId;
    }
    public Integer getUserCouponId(){
        return userCouponId;
    }
    public void setUserCouponId(Integer userCouponId){
    this.userCouponId=userCouponId;
    }
    public Date getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
    this.createTime=createTime;
    }
    public Date getUpdateTiem(){
        return updateTiem;
    }
    public void setUpdateTiem(Date updateTiem){
    this.updateTiem=updateTiem;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
    this.status=status;
    }
}

