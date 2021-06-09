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
    * mt_sms_sended_log 实体类
    * Created by zach
    * Thu Sep 19 15:37:11 CST 2019
    */ 
@Entity 
@Table(name = "mt_sms_sended_log")
public class MtSmsSendedLog implements Serializable{
   /**
    * 日志ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID", nullable = false, length = 11)
    private Integer logId;

   /**
    * 手机号 
    */ 
    @Column(name = "MOBILE_PHONE", length = 32)
    private String mobilePhone;

   /**
    * 短信内容 
    */ 
    @Column(name = "CONTENT", length = 1024)
    private String content;

   /**
    * 发送时间 
    */ 
    @Column(name = "SEND_TIME")
    private Date sendTime;

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

    public Integer getLogId(){
        return logId;
    }
    public void setLogId(Integer logId){
    this.logId=logId;
    }
    public String getMobilePhone(){
        return mobilePhone;
    }
    public void setMobilePhone(String mobilePhone){
    this.mobilePhone=mobilePhone;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
    this.content=content;
    }
    public Date getSendTime(){
        return sendTime;
    }
    public void setSendTime(Date sendTime){
    this.sendTime=sendTime;
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
}

