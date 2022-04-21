package com.fuint.base.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;

/**
 * mt_store 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Entity 
@Table(name = "mt_store")
public class TStore implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Long id;

   /**
    * 店铺名称
    */ 
    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    /**
     * 是否默认店铺
     */
    @Column(name = "IS_DEFAULT", nullable = false, length = 1)
    private String isDefault;

   /**
    * 联系人姓名 
    */ 
    @Column(name = "CONTACT", length = 30)
    private String contact;

   /**
    * 联系电话 
    */ 
    @Column(name = "PHONE", length = 20)
    private String phone;

    /**
     * 店铺地址
     */
    @Column(name = "ADDRESS", length = 100)
    private String address;

    /**
     * 营业时间
     */
    @Column(name = "HOURS", length = 100)
    private String hours;

   /**
    * 备注信息 
    */ 
    @Column(name = "DESCRIPTION", length = 2000)
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
    * 状态，A：正常；D：删除
    */ 
    @Column(name = "STATUS", length = 1)
    private String status;

    public Long getId(){
        return id;
    }
    public void setId(Long id){
    this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
    this.name=name;
    }
    public String getIsDefault(){
        return isDefault;
    }
    public void setIsDefault(String isDefault){
        this.isDefault=isDefault;
    }
    public String getContact(){
        return contact;
    }
    public void setContact(String contact){
    this.contact=contact;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
    this.phone=phone;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public String getHours(){
        return hours;
    }
    public void setHours(String hours){
        this.hours=hours;
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
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
    this.status=status;
    }
}

