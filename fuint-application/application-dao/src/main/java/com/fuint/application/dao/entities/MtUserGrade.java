package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

   /**
    * mt_user_grade 实体类
    * Created by zach
    * Tue May 18 22:35:56 GMT+08:00 2021
    */ 
@Entity 
@Table(name = "mt_user_grade")
public class MtUserGrade implements Serializable{
   /**
    * 自增ID 
    */ 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, length = 10)
	private Integer id;

   /**
    * 等级 
    */ 
	@Column(name = "GRADE", length = 2)
	private Integer grade;

   /**
    * 等级名称 
    */ 
	@Column(name = "NAME", length = 30)
	private String name;

   /**
    * 升级会员等级条件描述 
    */ 
	@Column(name = "CATCH_CONDITION", length = 255)
	private String catchCondition;

   /**
    * 升级会员等级条件，init:默认获取;pay:付费升级；frequency:消费次数；amount:累积消费金额升级 
    */ 
	@Column(name = "CATCH_TYPE", length = 30)
	private String catchType;

   /**
    * 达到升级条件的值 
    */ 
	@Column(name = "CATCH_VALUE", length = 10)
	private Integer catchValue;

   /**
    * 会员权益描述 
    */ 
	@Column(name = "USER_PRIVILEGE", length = 1000)
	private String userPrivilege;

   /**
    * 有效期 
    */ 
	@Column(name = "VALID_DAY", length = 10)
	private Integer validDay;

   /**
    * 享受折扣 
    */ 
	@Column(name = "DISCOUNT")
	private Float discount;

   /**
    * 积分加速 
    */ 
	@Column(name = "SPEED_POINT")
	private Float speedPoint;

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
	public Integer getGrade(){
		return grade;
	}
	public void setGrade(Integer grade){
	this.grade=grade;
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
	this.name=name;
	}
	public String getCatchCondition(){
		return catchCondition;
	}
	public void setCatchCondition(String catchCondition){
	this.catchCondition=catchCondition;
	}
	public String getCatchType(){
		return catchType;
	}
	public void setCatchType(String catchType){
	this.catchType=catchType;
	}
	public Integer getCatchValue(){
		return catchValue;
	}
	public void setCatchValue(Integer catchValue){
	this.catchValue=catchValue;
	}
	public String getUserPrivilege(){
		return userPrivilege;
	}
	public void setUserPrivilege(String userPrivilege){
	this.userPrivilege=userPrivilege;
	}
	public Integer getValidDay(){
		return validDay;
	}
	public void setValidDay(Integer validDay){
	this.validDay=validDay;
	}
	public Float getDiscount(){
		return discount;
	}
	public void setDiscount(Float discount){
	this.discount=discount;
	}
	public Float getSpeedPoint(){
		return speedPoint;
	}
	public void setSpeedPoint(Float speedPoint){
	this.speedPoint=speedPoint;
	}
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
	this.status=status;
	}
}

