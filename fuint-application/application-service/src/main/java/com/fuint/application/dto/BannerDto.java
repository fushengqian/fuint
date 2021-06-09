package com.fuint.application.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * BannerDto 实体类
 * Created by zach
 * Tue Apr 13 16:31:40 GMT+08:00 2021
 */
public class BannerDto implements Serializable {
   /**
    * 自增ID 
    */
	private Integer id;

   /**
    * 标题 
    */
	private String title;

   /**
    * 图片地址 
    */
	private String image;

   /**
    * 描述 
    */
	private String description;

   /**
    * 创建时间 
    */
	private Date createTime;

   /**
    * 更新时间 
    */
	private Date updateTime;

   /**
    * 最后操作人 
    */
	private String operator;

   /**
    * A：正常；D：删除 
    */
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

