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
    * mt_point 实体类
    * Created by zach
    * Tue May 18 23:09:52 GMT+08:00 2021
    */ 
@Entity 
@Table(name = "mt_point")
public class MtPoint implements Serializable{
   /**
    * 自增ID 
    */ 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, length = 10)
	private Integer id;

   /**
    * 用户ID 
    */ 
	@Column(name = "USER_ID", nullable = false, length = 10)
	private Integer userId;

   /**
    * 积分变化数量 
    */ 
	@Column(name = "AMOUNT", nullable = false, length = 10)
	private Integer amount;

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
    * 备注说明 
    */ 
	@Column(name = "DESCRIPTION", length = 200)
	private String description;

   /**
    * 状态，A正常；D作废 
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
	public Integer getAmount(){
		return amount;
	}
	public void setAmount(Integer amount){
	this.amount=amount;
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
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
	this.description=description;
	}
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
	this.status=status;
	}
}

