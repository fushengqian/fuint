package com.fuint.application.dao.entities;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

   /**
    * uv_coupon_info 实体类
    * Created by zach
    * Thu Sep 12 17:18:54 CST 2019
    */ 
@Entity 
@Table(name = "uv_coupon_info")
public class UvCouponInfo implements Serializable{

   /**
    * 自增ID 
    */
    @Id
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 编码
    */
   @Column(name = "CODE", nullable = false, length = 20)
   private String code;

   /**
    * 券ID 
    */
    @Column(name = "COUPON_ID", nullable = false, length = 10)
    private Integer couponId;

   /**
    * 用户ID 
    */ 
    @Column(name = "USER_ID", nullable = false, length = 10)
    private Integer userId;

   /**
    * 状态：A：未使用；B：已使用；C：已过期; D：已删除 
    */ 
    @Column(name = "COUPON_INFO_STATUS", nullable = false, length = 1)
    private String couponInfoStatus;

   /**
    *  
    */ 
    @Column(name = "COUPON_INFO_STATUS_DESC", nullable = false)
    private String couponInfoStatusDesc;

   /**
    * 使用店铺ID
    */ 
    @Column(name = "STORE_ID", length = 11)
    private Integer storeId;

   /**
    * 使用时间 
    */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "USED_TIME")
    private Date usedTime;

   /**
    * 创建时间 
    */
   @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_TIME")
    private Date createTime;

   /**
    * 更新时间 
    */
   @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

   /**
    * 手机号码 
    */ 
    @Column(name = "MOBILE", nullable = false, length = 16)
    private String mobile;

   /**
    * 适用店铺id列表
    */
    @Column(name = "SUIT_STORE_IDS", nullable = false, length = 256)
    private String suitStoreIds;

   /**
    * 真实姓名 
    */ 
    @Column(name = "REAL_NAME", length = 30)
    private String realName;

   /**
    * 券名称
    */ 
    @Column(name = "coupon_name", nullable = false, length = 100)
    private String couponName;

   /**
    * 券效果图
    */
    @Column(name = "coupon_image", nullable = false, length = 100)
    private String couponImage;

   /**
    * 面额 
    */ 
    @Column(name = "MONEY")
    private BigDecimal money;

   /**
    * 分组ID
    */ 
    @Column(name = "GROUP_ID", nullable = false, length = 10)
    private Integer groupId;

   /**
    * A：正常；D：删除 
    */ 
    @Column(name = "coupon_status", length = 1)
    private String couponStatus;

   /**
    * 开始有效期 
    */ 
    @Column(name = "BEGIN_TIME")
    private Date beginTime;

   /**
    * 结束有效期 
    */ 
    @Column(name = "END_TIME")
    private Date endTime;

   /**
    * 分组名称
    */ 
    @Column(name = "coupon_group_name", length = 100)
    private String couponGroupName;

   /**
    * 店铺名称
    */ 
    @Column(name = "store_Name", length = 50)
    private String storeName;


    /**
    * 发券量
    */
    @Transient
    private int couponTotal;
   /**
    * 未使用量
    */
    @Transient
    private int unUsedTotal;
    /**
    * 使用量
    */
    @Transient
    private int usedTotal;

    /**
     * 过期量
     */
    @Transient
    private int expireTotal;

    /**
     * 作废量
     */
    @Transient
    private int disableTotal;

    /**
     * 导入UUID
     */
    @Column(name = "UUID", nullable = false, length = 50)
    private String uuid;

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
    public Integer getCouponId(){
        return couponId;
    }
    public void setCouponId(Integer couponId){
    this.couponId=couponId;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
    this.userId=userId;
    }
    public String getCouponInfoStatus(){
        return couponInfoStatus;
    }
    public void setCouponInfoStatus(String couponInfoStatus){
    this.couponInfoStatus=couponInfoStatus;
    }
    public String getCouponInfoStatusDesc(){
        return couponInfoStatusDesc;
    }
    public void setCouponInfoStatusDesc(String couponInfoStatusDesc){
    this.couponInfoStatusDesc=couponInfoStatusDesc;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
    this.storeId=storeId;
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
    public String getCouponName(){
        return couponName;
    }
    public void setCouponName(String couponName){
    this.couponName=couponName;
    }
    public String getCouponImage(){
           return couponImage;
       }
    public void setCouponImage(String couponImage){
           this.couponImage=couponImage;
    }
    public BigDecimal getMoney(){
        return money;
    }
    public void setMoney(BigDecimal money){
    this.money=money;
    }
    public Integer getGroupId(){
        return groupId;
    }
    public void setGroupId(Integer groupId){
    this.groupId=groupId;
    }
    public String getCouponStatus(){
        return couponStatus;
    }
    public void setCouponStatus(String couponStatus){
    this.couponStatus=couponStatus;
    }
    public Date getBeginTime(){
        return beginTime;
    }
    public void setBeginTime(Date beginTime){
    this.beginTime=beginTime;
    }
    public Date getEndTime(){
        return endTime;
    }
    public void setEndTime(Date endTime){
    this.endTime=endTime;
    }
    public String getCouponGroupName(){
        return couponGroupName;
    }
    public void setCouponGroupName(String couponGroupName){
    this.couponGroupName=couponGroupName;
    }
    public String getStoreName(){
        return storeName;
    }
    public void setStoreName(String storeName){
    this.storeName=storeName;
    }

    public String getSuitStoreIds(){
       return suitStoreIds;
   }
    public void setSuitStoreIds(String suitStoreIds){
       this.suitStoreIds=suitStoreIds;
   }
    public int getCouponTotal() {
       return couponTotal;
   }
    public void setCouponTotal(int couponTotal) {
       this.couponTotal = couponTotal;
   }

    public int getUnUsedTotal() {
       return unUsedTotal;
   }

    public void setUnUsedTotal(int unUsedTotal) {
       this.unUsedTotal = unUsedTotal;
   }

    public int getUsedTotal() {
       return usedTotal;
   }

    public void setUsedTotal(int usedTotal) {
       this.usedTotal = usedTotal;
   }

    public int getExpireTotal() {
       return expireTotal;
   }

    public void setExpireTotal(int expireTotal) {
       this.expireTotal = expireTotal;
   }

    public int getDisableTotal() {
       return disableTotal;
   }

    public void setDisableTotal(int disableTotal) {
       this.disableTotal = disableTotal;
   }

    public String getUuid(){
       return uuid;
   }
    public void setUuid(String uuid){
           this.uuid=uuid;
       }
  }