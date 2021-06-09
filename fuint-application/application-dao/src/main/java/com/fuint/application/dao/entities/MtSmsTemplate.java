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
    * mt_sms_template 实体类
    * Created by zach
    * Sat Apr 18 18:39:27 GMT+08:00 2020
    */ 
@Entity 
@Table(name = "mt_sms_template")
public class MtSmsTemplate implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 名称 
    */ 
    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

   /**
    * 英文名称 
    */ 
    @Column(name = "UNAME", nullable = false, length = 50)
    private String uname;

   /**
    * 编码 
    */ 
    @Column(name = "CODE", nullable = false, length = 30)
    private String code;

   /**
    * 内容 
    */ 
    @Column(name = "CONTENT", nullable = false, length = 255)
    private String content;

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
    * 状态：A激活；N禁用 
    */ 
    @Column(name = "STATUS", nullable = false, length = 1)
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
    public String getUname(){
        return uname;
    }
    public void setUname(String uname){
    this.uname=uname;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
    this.code=code;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
    this.content=content;
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

