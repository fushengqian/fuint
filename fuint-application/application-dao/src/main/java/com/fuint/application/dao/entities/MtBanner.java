package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;
import java.sql.*;

   /**
    * mt_banner 实体类
    * Created by zach
    * Thu Apr 22 10:35:28 GMT+08:00 2021
    */ 
@Entity 
@Table(name = "mt_banner")
public class MtBanner implements Serializable{
   /**
    * 自增ID 
    */ 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, length = 10)
	private Integer id;

   /**
    * 标题 
    */ 
	@Column(name = "TITLE", length = 100)
	private String title;

   /**
    * 链接地址 
    */ 
	@Column(name = "URL", length = 100)
	private String url;

   /**
    * 图片地址 
    */ 
	@Column(name = "IMAGE", length = 200)
	private String image;

   /**
    * 描述 
    */ 
	@Column(name = "DESCRIPTION")
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
    * A：正常；D：删除 
    */ 
	@Column(name = "STATUS", length = 1)
	private String status;

	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
	this.id=id;
	}
	public String getTitle(){
		return title;
	}
	public void setTitle(String title){
	this.title=title;
	}
	public String getUrl(){
		return url;
	}
	public void setUrl(String url){
	this.url=url;
	}
	public String getImage(){
		return image;
	}
	public void setImage(String image){
	this.image=image;
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

