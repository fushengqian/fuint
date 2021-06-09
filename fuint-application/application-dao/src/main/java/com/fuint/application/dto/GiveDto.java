package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;

   /**
    * mt_give 实体类
    * Created by zach
    * Sat Oct 12 15:34:19 GMT+08:00 2019
    */
public class GiveDto implements Serializable{
   /**
    * 自增 
    */
    private Integer id;

   /**
    * 获赠者用户ID
    */
    private Integer userId;

   /**
    * 赠送者用户ID 
    */
    private Integer giveUserId;

   /**
    * 赠予对象手机号 
    */
    private String mobile;

   /**
    * 赠予对象FuInt账号
    */
    private String hnaAccount;

   /**
    * 用户手机 
    */
    private String userMobile;

   /**
    * 用户FuInt账号
    */
    private String userHnaAccount;

   /**
    * 分组ID，逗号隔开
    */
    private String groupIds;

   /**
    * 分组名称，逗号隔开
    */
    private String groupNames;

   /**
    * 券ID，逗号隔开 
    */
    private String couponIds;

   /**
    * 券名称，逗号隔开 
    */
    private String couponNames;

   /**
    * 数量 
    */
    private Integer num;

   /**
    * 总金额 
    */
    private BigDecimal money;

   /**
    * 备注 
    */
    private String note;

   /**
    * 留言
    */
   private String message;

   /**
    * 赠送时间 
    */
    private String createTime;

   /**
    * 更新时间 
    */
    private String updateTime;

   /**
    * 状态，A正常；C取消 
    */
    private String status;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public Integer getUserId(){
        return userId;
    }
    public void setUserId(Integer userId){
    this.userId=userId;
    }
    public Integer getGiveUserId(){
        return giveUserId;
    }
    public void setGiveUserId(Integer giveUserId){
    this.giveUserId=giveUserId;
    }
    public String getMobile(){
        return mobile;
    }
    public void setMobile(String mobile){
    this.mobile=mobile;
    }
    public String getHnaAccount(){
        return hnaAccount;
    }
    public void setHnaAccount(String hnaAccount){
    this.hnaAccount=hnaAccount;
    }
    public String getUserMobile(){
        return userMobile;
    }
    public void setUserMobile(String userMobile){
    this.userMobile=userMobile;
    }
    public String getUserHnaAccount(){
        return userHnaAccount;
    }
    public void setUserHnaAccount(String userHnaAccount){
    this.userHnaAccount=userHnaAccount;
    }
    public String getGroupIds(){
        return groupIds;
    }
    public void setGroupIds(String groupIds){
    this.groupIds=groupIds;
    }
    public String getGroupNames(){
        return groupNames;
    }
    public void setGroupNames(String groupNames){
    this.groupNames=groupNames;
    }
    public String getCouponIds(){
        return couponIds;
    }
    public void setCouponIds(String couponIds){
    this.couponIds=couponIds;
    }
    public String getCouponNames(){
        return couponNames;
    }
    public void setCouponNames(String couponNames){
    this.couponNames=couponNames;
    }
    public Integer getNum(){
        return num;
    }
    public void setNum(Integer num){
    this.num=num;
    }
    public BigDecimal getMoney(){
        return money;
    }
    public void setMoney(BigDecimal money){
    this.money=money;
    }
    public String getNote(){
        return note;
    }
    public void setNote(String note){
    this.note=note;
    }
    public String getMessage(){
       return message;
   }
    public void setMessage(String message){
           this.message=message;
       }
    public String getCreateTime(){
        return createTime;
    }
    public void setCreateTime(String createTime){
    this.createTime=createTime;
    }
    public String getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(String updateTime){
    this.updateTime=updateTime;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
    this.status=status;
    }
}

