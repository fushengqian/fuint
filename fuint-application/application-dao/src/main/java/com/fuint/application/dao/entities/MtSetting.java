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
    * mt_setting 实体类
    * Created by zach
    * Tue May 18 22:32:02 GMT+08:00 2021
    */ 
@Entity 
@Table(name = "mt_setting")
public class MtSetting implements Serializable{
   /**
    * 自增ID 
    */ 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, length = 10)
	private Integer id;

   /**
    * 类型 
    */ 
	@Column(name = "TYPE", nullable = false, length = 10)
	private String type;

   /**
    * 配置项 
    */ 
	@Column(name = "NAME", nullable = false, length = 50)
	private String name;

   /**
    * 配置值 
    */ 
	@Column(name = "VALUE", nullable = false, length = 1000)
	private String value;

   /**
    * 配置说明 
    */ 
	@Column(name = "DESCRIPTION", length = 200)
	private String description;

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
    * 最后操作人 
    */ 
	@Column(name = "OPERATOR", length = 30)
	private String operator;

   /**
    * 状态 A启用；D禁用 
    */ 
	@Column(name = "STATUS", length = 1)
	private String status;

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
	public String getName(){
		return name;
	}
	public void setName(String name){
	this.name=name;
	}
	public String getValue(){
		return value;
	}
	public void setValue(String value){
	this.value=value;
	}
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
	this.description=description;
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
	public String getOperator(){
		return operator;
	}
	public void setOperator(String operator){
	this.operator=operator;
	}
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
	this.status=status;
	}
}

