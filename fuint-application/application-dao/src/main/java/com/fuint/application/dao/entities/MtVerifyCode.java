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
    * mt_verify_code 实体类
    * Created by zach
    * Tue Aug 20 10:26:51 CST 2019
    */ 
@Entity 
@Table(name = "mt_verify_code")
public class MtVerifyCode implements Serializable{
   /**
    * 自增id 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 20)
    private Long id;

   /**
    * 手机号 
    */ 
    @Column(name = "mobile", length = 16)
    private String mobile;

   /**
    * 验证码 
    */ 
    @Column(name = "verifyCode", length = 6)
    private String verifycode;

   /**
    * 创建时间 
    */ 
    @Column(name = "addTime")
    private Date addtime;

   /**
    * 过期时间 
    */ 
    @Column(name = "expireTime")
    private Date expiretime;

   /**
    * 使用时间 
    */ 
    @Column(name = "usedTime")
    private Date usedtime;

   /**
    * 可用状态 0未用 1已用 
    */ 
    @Column(name = "validFlag", length = 1)
    private String validflag;

    public Long getId(){
        return id;
    }
    public void setId(Long id){
    this.id=id;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public String getVerifycode(){
        return verifycode;
    }
    public void setVerifycode(String verifycode){
    this.verifycode=verifycode;
    }
    public Date getAddtime(){
        return addtime;
    }
    public void setAddtime(Date addtime){
    this.addtime=addtime;
    }
    public Date getExpiretime(){
        return expiretime;
    }
    public void setExpiretime(Date expiretime){
    this.expiretime=expiretime;
    }
    public Date getUsedtime(){
        return usedtime;
    }
    public void setUsedtime(Date usedtime){
    this.usedtime=usedtime;
    }
    public String getValidflag(){
        return validflag;
    }
    public void setValidflag(String validflag){
    this.validflag=validflag;
    }
}

