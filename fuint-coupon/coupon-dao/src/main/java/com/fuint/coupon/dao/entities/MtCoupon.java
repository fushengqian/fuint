package com.fuint.coupon.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

   /**
    * mt_coupon 实体类
    * Created by zach
    * Wed Apr 29 17:37:41 GMT+08:00 2020
    */ 
@Entity 
@Table(name = "mt_coupon")
public class MtCoupon implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 分组ID
    */ 
    @Column(name = "GROUP_ID", nullable = false, length = 10)
    private Integer groupId;

   /**
    * 券名称 
    */ 
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

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
    * 面额 
    */ 
    @Column(name = "MONEY")
    private BigDecimal money;

   /**
    * 数量 
    */ 
    @Column(name = "TOTAL", length = 10)
    private Integer total;

   /**
    * 例外日期，逗号隔开。周末：weekend；其他：2019-01-02~2019-02-09 
    */ 
    @Column(name = "EXCEPT_TIME", length = 500)
    private String exceptTime;

   /**
    * 所属店铺ID,逗号隔开 
    */ 
    @Column(name = "STORE_IDS", length = 100)
    private String storeIds;

   /**
    * 描述信息 
    */ 
    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

   /**
    * 效果图片 
    */ 
    @Column(name = "IMAGE", length = 100)
    private String image;

   /**
    * 后台备注 
    */ 
    @Column(name = "REMARKS", length = 1000)
    private String remarks;

   /**
    * 获取券的规则 
    */ 
    @Column(name = "IN_RULE", length = 1000)
    private String inRule;

   /**
    * 核销券的规则 
    */ 
    @Column(name = "OUT_RULE", length = 1000)
    private String outRule;

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
    * A：正常；D：删除 
    */ 
    @Column(name = "STATUS", length = 1)
    private String status;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public Integer getGroupId(){
        return groupId;
    }
    public void setGroupId(Integer groupId){
    this.groupId=groupId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
    this.name=name;
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
    public BigDecimal getMoney(){
        return money;
    }
    public void setMoney(BigDecimal money){
    this.money=money;
    }
    public Integer getTotal(){
        return total;
    }
    public void setTotal(Integer total){
    this.total=total;
    }
    public String getExceptTime(){
        return exceptTime;
    }
    public void setExceptTime(String exceptTime){
    this.exceptTime=exceptTime;
    }
    public String getStoreIds(){
        return storeIds;
    }
    public void setStoreIds(String storeIds){
    this.storeIds=storeIds;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
    this.description=description;
    }
    public String getImage(){
        return image;
    }
    public void setImage(String image){
    this.image=image;
    }
    public String getRemarks(){
        return remarks;
    }
    public void setRemarks(String remarks){
    this.remarks=remarks;
    }
    public String getInRule(){
        return inRule;
    }
    public void setInRule(String inRule){
    this.inRule=inRule;
    }
    public String getOutRule(){
        return outRule;
    }
    public void setOutRule(String outRule){
    this.outRule=outRule;
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

