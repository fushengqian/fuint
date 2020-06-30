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
    * mt_user_coupon 实体类
    * Created by zach
    * Tue Sep 03 11:34:25 GMT+08:00 2019
    */ 
@Entity 
@Table(name = "mt_user_coupon")
public class MtUserCoupon implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

    /**
     * 编码
     */
    @Column(name = "CODE", nullable = false, length = 20)
    private String code;

   /**
    * 分组ID
    */ 
    @Column(name = "GROUP_ID", nullable = false, length = 10)
    private Integer groupId;

   /**
    * 优惠券ID
    */ 
    @Column(name = "COUPON_ID", nullable = false, length = 10)
    private Integer couponId;

   /**
    * 用户手机号码 
    */ 
    @Column(name = "MOBILE", nullable = false, length = 20)
    private String mobile;

   /**
    * 用户ID 
    */ 
    @Column(name = "USER_ID", nullable = false, length = 10)
    private Integer userId;

   /**
    * 使用店铺ID
    */ 
    @Column(name = "STORE_ID", length = 11)
    private Integer storeId;

   /**
    * 状态：1：未领取；2：已领取;3：已使用 
    */ 
    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

   /**
    * 使用时间 
    */ 
    @Column(name = "USED_TIME")
    private Date usedTime;

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
    * UUID
    */
   @Column(name = "UUID", length = 50)
   private String uuid;

   /**
    * operator
    */
   @Column(name = "OPERATOR", length = 30)
   private String operator;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getCode(){
           return code;
       }
    public void setCode(String code){
           this.code=code;
       }
    public Integer getGroupId(){
        return groupId;
    }
    public void setGroupId(Integer groupId){
    this.groupId=groupId;
    }
    public Integer getCouponId(){
        return couponId;
    }
    public void setCouponId(Integer couponId){
    this.couponId=couponId;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
    this.userId=userId;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
    this.storeId=storeId;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
    this.status=status;
    }
    public Date getUsedTime(){
        return usedTime;
    }
    public void setUsedTime(Date usedTime){
    this.usedTime=usedTime;
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
    public String getUuid(){
       return uuid;
   }
    public void setUuid(String uuid){
       this.uuid=uuid;
    }
    public String getOperator(){
           return operator;
       }
    public void setOperator(String operator){
           this.operator=operator;
       }
}

