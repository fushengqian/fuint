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
    * mt_order 实体类
    * Created by zach
    * Wed May 05 22:00:40 GMT+08:00 2021
    */ 
@Entity 
@Table(name = "mt_order")
public class MtOrder implements Serializable{
   /**
    * 自增ID 
    */ 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, length = 10)
	private Integer id;

   /**
    * 订单类型 
    */ 
	@Column(name = "TYPE", length = 30)
	private String type;

   /**
    * 订单号 
    */ 
	@Column(name = "ORDER_SN", nullable = false, length = 32)
	private String orderSn;

   /**
    * 卡券ID 
    */ 
	@Column(name = "COUPON_ID", length = 10)
	private Integer couponId;

   /**
    * 用户ID 
    */ 
	@Column(name = "USER_ID", nullable = false, length = 10)
	private Integer userId;

   /**
    * 订单金额 
    */ 
	@Column(name = "AMOUNT")
	private BigDecimal amount;

   /**
    * 订单参数 
    */ 
	@Column(name = "PARAM", length = 500)
	private String param;

   /**
    * 用户备注 
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
	public String getType(){
		return type;
	}
	public void setType(String type){
	this.type=type;
	}
	public String getOrderSn(){
		return orderSn;
	}
	public void setOrderSn(String orderSn){
	this.orderSn=orderSn;
	}
	public Integer getCouponId(){
		return couponId;
	}
	public void setCouponId(Integer couponId){
	this.couponId=couponId;
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
	public String getParam(){
		return param;
	}
	public void setParam(String param){
	this.param=param;
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
	public String getOperator(){
		return operator;
	}
	public void setOperator(String operator){
	this.operator=operator;
	}
}

