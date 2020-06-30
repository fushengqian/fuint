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
    * t_account 实体类
    * Created by zach
    * Tue Sep 17 21:50:18 CST 2019
    */ 
@Entity 
@Table(name = "t_account")
public class TAccount implements Serializable{
   /**
    * 主键id 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acct_id", nullable = false, length = 11)
    private Integer acctId;

   /**
    * 账户编码 
    */ 
    @Column(name = "account_key", nullable = false, length = 23)
    private String accountKey;

   /**
    * 账户名称 
    */ 
    @Column(name = "account_name", nullable = false, length = 20)
    private String accountName;

   /**
    * 密码 
    */ 
    @Column(name = "password", nullable = false, length = 100)
    private String password;

   /**
    * 0 无效 1 有效 
    */ 
    @Column(name = "account_status", nullable = false, length = 11)
    private Integer accountStatus;

   /**
    * 0 未激活 1已激活 
    */ 
    @Column(name = "is_active", nullable = false, length = 11)
    private Integer isActive;

   /**
    * 创建时间 
    */ 
    @Column(name = "create_date", nullable = false)
    private Date createDate;

   /**
    * 修改时间 
    */ 
    @Column(name = "modify_date", nullable = false)
    private Date modifyDate;

   /**
    * 随机码 
    */ 
    @Column(name = "salt", nullable = false, length = 64)
    private String salt;

   /**
    *  
    */ 
    @Column(name = "role_ids", length = 100)
    private String roleIds;

   /**
    *  
    */ 
    @Column(name = "locked", nullable = false, length = 1)
    private Integer locked;

   /**
    * 所属平台 
    */ 
    @Column(name = "owner_id", length = 20)
    private Integer ownerId;

   /**
    *  
    */ 
    @Column(name = "real_name", length = 255)
    private String realName;

   /**
    * 管辖店铺id  : -1 代表全部
    */ 
    @Column(name = "store_id", length = 11)
    private Integer storeId;

   /**
    * 管辖店铺名称
    */ 
    @Column(name = "store_name", length = 255)
    private String storeName;

    public Integer getAcctId(){
        return acctId;
    }
    public void setAcctId(Integer acctId){
    this.acctId=acctId;
    }
    public String getAccountKey(){
        return accountKey;
    }
    public void setAccountKey(String accountKey){
    this.accountKey=accountKey;
    }
    public String getAccountName(){
        return accountName;
    }
    public void setAccountName(String accountName){
    this.accountName=accountName;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
    this.password=password;
    }
    public Integer getAccountStatus(){
        return accountStatus;
    }
    public void setAccountStatus(Integer accountStatus){
    this.accountStatus=accountStatus;
    }
    public Integer getIsActive(){
        return isActive;
    }
    public void setIsActive(Integer isActive){
    this.isActive=isActive;
    }
    public Date getCreateDate(){
        return createDate;
    }
    public void setCreateDate(Date createDate){
    this.createDate=createDate;
    }
    public Date getModifyDate(){
        return modifyDate;
    }
    public void setModifyDate(Date modifyDate){
    this.modifyDate=modifyDate;
    }
    public String getSalt(){
        return salt;
    }
    public void setSalt(String salt){
    this.salt=salt;
    }
    public String getRoleIds(){
        return roleIds;
    }
    public void setRoleIds(String roleIds){
    this.roleIds=roleIds;
    }
    public Integer getLocked(){
        return locked;
    }
    public void setLocked(Integer locked){
    this.locked=locked;
    }
    public Integer getOwnerId(){
        return ownerId;
    }
    public void setOwnerId(Integer ownerId){
    this.ownerId=ownerId;
    }
    public String getRealName(){
        return realName;
    }
    public void setRealName(String realName){
    this.realName=realName;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
    this.storeId=storeId;
    }
    public String getStoreName(){
        return storeName;
    }
    public void setStoreName(String storeName){
    this.storeName=storeName;
    }
}

