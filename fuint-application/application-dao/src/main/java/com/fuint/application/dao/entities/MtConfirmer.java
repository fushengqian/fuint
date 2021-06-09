package com.fuint.application.dao.entities;

import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

   /**
    * mt_confirmer 实体类
    * Created by zach
    * Tue Sep 10 16:40:57 CST 2019
    */ 
@Entity 
@Table(name = "mt_confirmer")
public class MtConfirmer implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 手机号码 
    */ 
    @Column(name = "MOBILE", nullable = false, length = 16)
    private String mobile;

   /**
    * 真实姓名 
    */ 
    @Column(name = "REAL_NAME", length = 30)
    private String realName;

   /**
    * 微信号 
    */ 
    @Column(name = "WECHAT", length = 64)
    private String wechat;

   /**
    * 对应的核销店铺id
    */ 
    @Column(name = "STORE_ID", length = 11)
    private Integer storeId;

   /**
    * 创建时间 
    */ 
    @Column(name = "CREATE_TIME")
    private Date createTime;

   /**
    * 更新时间 
    */ 
    @Column(name = "UPDATE_TIME")
    @LastModifiedDate
    private Date updateTime;

   /**
    * 审核状态，A：审核通过；U：未审核；D：无效; N:禁用 
    */ 
    @Column(name = "Audited_STATUS", length = 1)
    private String auditedStatus;

   /**
    * 审核时间 
    */ 
    @Column(name = "Audited_TIME")
    private Date auditedTime;

   /**
    * 备注 
    */ 
    @Column(name = "DESCRIPTION", length = 255)
    private String description;

   /**
    * 对应前端用户id,审核通过时，记录到mt_user表 
    */ 
    @Column(name = "User_ID", length = 11)
    private Integer userId;


    /**
    * 店铺名称
    */
    @Transient
    private String storeName;

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
    public String getRealName(){
        return realName;
    }
    public void setRealName(String realName){
    this.realName=realName;
    }
    public String getWechat(){
        return wechat;
    }
    public void setWechat(String wechat){
    this.wechat=wechat;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
    this.storeId=storeId;
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
    public String getAuditedStatus(){
        return auditedStatus;
    }
    public void setAuditedStatus(String auditedStatus){
    this.auditedStatus=auditedStatus;
    }
    public Date getAuditedTime(){
        return auditedTime;
    }
    public void setAuditedTime(Date auditedTime){
    this.auditedTime=auditedTime;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
    this.description=description;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
    this.userId=userId;
    }
    public String getStoreName(){
           return storeName;
       }
    public void setStoreName(String storeName){
           this.storeName=storeName;
       }
}

