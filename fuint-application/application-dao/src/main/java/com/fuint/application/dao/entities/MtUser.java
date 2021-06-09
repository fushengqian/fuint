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
    * mt_user 实体类
    * Created by zach
    * Mon Mar 15 15:32:28 GMT+08:00 2021
    */ 
@Entity 
@Table(name = "mt_user")
public class MtUser implements Serializable{
   /**
    * 会员ID 
    */ 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, length = 10)
	private Integer id;

   /**
    * 姓名 
    */ 
	@Column(name = "REAL_NAME", length = 30)
	private String realName;

   /**
    * 手机号码 
    */ 
	@Column(name = "MOBILE", length = 20)
	private String mobile;

   /**
    * 证件号码 
    */ 
	@Column(name = "IDCARD", length = 20)
	private String idcard;

   /**
    * 等级
    */ 
	@Column(name = "GRADE_ID", length = 10)
	private String gradeId;

   /**
    * 性别 0男；1女 
    */ 
	@Column(name = "SEX", length = 1)
	private Integer sex;

   /**
    * 出生日期 
    */ 
	@Column(name = "BIRTHDAY", length = 20)
	private String birthday;

   /**
    * 车牌号 
    */ 
	@Column(name = "CAR_NO", length = 10)
	private String carNo;

   /**
    * 密码 
    */ 
	@Column(name = "PASSWORD", length = 32)
	private String password;

   /**
    * salt 
    */ 
	@Column(name = "SALT", length = 4)
	private String salt;

   /**
    * 地址 
    */ 
	@Column(name = "ADDRESS", length = 100)
	private String address;

   /**
    * 积分 
    */ 
	@Column(name = "POINT", length = 10)
	private Integer point;

   /**
    * 创建时间 
    */ 
	@Column(name = "CREATE_TIME")
	private Date createTime;

   /**
    * 更新时间 
    */ 
	@Column(name = "UPDATE_TIME", nullable = false)
	private Date updateTime;

   /**
    * 状态，A：激活；N：禁用；D：删除 
    */ 
	@Column(name = "STATUS", length = 1)
	private String status;

   /**
    * 是否处理 
    */ 
	@Column(name = "IS_DEAL", length = 1)
	private Byte isDeal;

   /**
    * 备注信息 
    */ 
	@Column(name = "DESCRIPTION", length = 255)
	private String description;

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
	public String getRealName(){
		return realName;
	}
	public void setRealName(String realName){
	this.realName=realName;
	}
	public String getMobile(){
		return mobile;
	}
	public void setMobile(String mobile){
	this.mobile=mobile;
	}
	public String getIdcard(){
		return idcard;
	}
	public void setIdcard(String idcard){
	this.idcard=idcard;
	}
	public String getGradeId(){
		return gradeId;
	}
	public void setGradeId(String gradeId){
	this.gradeId=gradeId;
	}
	public Integer getSex(){
		return sex;
	}
	public void setSex(Integer sex){
	this.sex=sex;
	}
	public String getBirthday(){
		return birthday;
	}
	public void setBirthday(String birthday){
	this.birthday=birthday;
	}
	public String getCarNo(){
		return carNo;
	}
	public void setCarNo(String carNo){
	this.carNo=carNo;
	}
	public String getPassword(){
		return password;
	}
	public void setPassword(String password){
	this.password=password;
	}
	public String getSalt(){
		return salt;
	}
	public void setSalt(String salt){
	this.salt=salt;
	}
	public String getAddress(){
		return address;
	}
	public void setAddress(String address){
	this.address=address;
	}
	public Integer getPoint(){
		return point;
	}
	public void setPoint(Integer point){
	this.point=point;
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
	public Byte getIsDeal(){
		return isDeal;
	}
	public void setIsDeal(Byte isDeal){
	this.isDeal=isDeal;
	}
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
	this.description=description;
	}
	public String getOperator(){
		return operator;
	}
	public void setOperator(String operator){
	this.operator=operator;
	}
}

