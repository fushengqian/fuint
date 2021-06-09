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
    * mt_confirm_log 实体类
    * Created by shude.wang
    * Fri Oct 18 10:03:14 CST 2019
    */ 
@Entity 
@Table(name = "mt_confirm_log")
public class MtConfirmLog implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 编码 
    */ 
    @Column(name = "CODE", nullable = false, length = 32)
    private String code;

   /**
    * 用户券ID 
    */ 
    @Column(name = "USER_COUPON_ID", nullable = false, length = 10)
    private Integer userCouponId;

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
    * 用户卡券所属用户id
    */ 
    @Column(name = "USER_ID", nullable = false, length = 10)
    private Integer userId;

   /**
    * 核销者用户ID 
    */ 
    @Column(name = "OPERATOR_USER_ID", length = 11)
    private Integer operatorUserId;

   /**
    * 核销店铺ID
    */ 
    @Column(name = "STORE_ID", nullable = false, length = 10)
    private Integer storeId;

   /**
    * 状态，A正常核销；D：撤销使用 
    */ 
    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

   /**
    * 撤销时间 
    */ 
    @Column(name = "CANCEL_TIME")
    private Date cancelTime;

   /**
    * 最后操作人 
    */ 
    @Column(name = "OPERATOR", length = 128)
    private String operator;

   /**
    * 操作来源user_id对应表t_account 还是 mt_user 
    */ 
    @Column(name = "OPERATOR_FROM", length = 30)
    private String operatorFrom;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
    this.code=code;
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
    public Date getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(Date updateTime){
    this.updateTime=updateTime;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
    this.userId=userId;
    }
    public Integer getOperatorUserId(){
        return operatorUserId;
    }
    public void setOperatorUserId(Integer operatorUserId){
    this.operatorUserId=operatorUserId;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
    this.storeId=storeId;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
    this.status=status;
    }
    public Date getCancelTime(){
        return cancelTime;
    }
    public void setCancelTime(Date cancelTime){
    this.cancelTime=cancelTime;
    }
    public String getOperator(){
        return operator;
    }
    public void setOperator(String operator){
    this.operator=operator;
    }
    public String getOperatorFrom(){
        return operatorFrom;
    }
    public void setOperatorFrom(String operatorFrom){
    this.operatorFrom=operatorFrom;
    }
}

