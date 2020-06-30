package com.fuint.coupon.dao.entities;

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
 * Thu Aug 08 11:15:42 CST 2019
 */
@Entity 
@Table(name = "mt_user")
public class MtUser implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 手机号码 
    */ 
    @Column(name = "MOBILE", nullable = false, length = 16)
    private String mobile;

   /**
    * 真实姓名 
    */ 
    @Column(name = "REAL_NAME", length = 30)
    private String realName;

   /**
    * 出生日期
    */
   @Column(name = "BIRTHDAY", length = 20)
   private String birthday;

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
    * 状态，1：激活；2：禁用；3：删除 
    */ 
    @Column(name = "STATUS", length = 1)
    private String status;

   /**
    * 备注 
    */ 
    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public String getRealName(){
        return realName;
    }
    public void setRealName(String realName){
    this.realName=realName;
    }
    public String getBirthday(){
           return birthday;
       }
    public void setBirthday(String birthday){
           this.birthday=birthday;
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
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
    this.description=description;
    }
}

