package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

   /**
    * mt_user_group 实体类
    * Created by zach
    * Mon Mar 15 15:34:23 GMT+08:00 2021
    */ 
@Entity 
@Table(name = "mt_user_group")
public class MtUserGroup implements Serializable{
   /**
    * 自增ID 
    */ 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, length = 10)
	private Integer id;

   /**
    * 分组名称 
    */ 
	@Column(name = "NAME", length = 30)
	private String name;

   /**
    * 最少积分 
    */ 
	@Column(name = "MIN_POINT", length = 10)
	private Integer minPoint;

   /**
    * 最多积分 
    */ 
	@Column(name = "MAX_POINT", length = 10)
	private Integer maxPoint;

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
	public String getName(){
		return name;
	}
	public void setName(String name){
	this.name=name;
	}
	public Integer getMinPoint(){
		return minPoint;
	}
	public void setMinPoint(Integer minPoint){
	this.minPoint=minPoint;
	}
	public Integer getMaxPoint(){
		return maxPoint;
	}
	public void setMaxPoint(Integer maxPoint){
	this.maxPoint=maxPoint;
	}
	public String getStatus(){
		return status;
	}
	public void setStatus(String status){
	this.status=status;
	}
}

