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
 * mt_refund 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */ 
@Entity 
@Table(name = "mt_refund")
public class MtRefund implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 11)
    private Integer id;

   /**
    * 订单ID 
    */ 
    @Column(name = "ORDER_ID", nullable = false, length = 11)
    private Integer orderId;

   /**
    * 店铺ID 
    */ 
    @Column(name = "STORE_ID", length = 10)
    private Integer storeId;

   /**
    * 会员ID 
    */ 
    @Column(name = "USER_ID", nullable = false, length = 11)
    private Integer userId;

   /**
    * 退款金额 
    */ 
    @Column(name = "AMOUNT")
    private BigDecimal amount;

   /**
    * 售后类型 
    */ 
    @Column(name = "TYPE", length = 20)
    private String type;

   /**
    * 退款备注 
    */ 
    @Column(name = "REMARK", length = 500)
    private String remark;

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

   /**
    * 图片 
    */ 
    @Column(name = "IMAGES", length = 1000)
    private String images;

   /**
    * 最后操作人 
    */ 
    @Column(name = "OPERATOR", length = 30)
    private String operator;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public Integer getOrderId(){
        return orderId;
    }
    public void setOrderId(Integer orderId){
        this.orderId=orderId;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
        this.storeId=storeId;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
        this.userId=userId;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public void setAmount(BigDecimal amount){
        this.amount=amount;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type=type;
    }
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
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
    public String getImages(){
        return images;
    }
    public void setImages(String images){
        this.images=images;
    }
    public String getOperator(){
        return operator;
    }
    public void setOperator(String operator){
        this.operator=operator;
    }
}

