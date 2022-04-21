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
 * mt_open_gift 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */ 
@Entity 
@Table(name = "mt_open_gift")
public class MtOpenGift implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 门店ID 
    */ 
    @Column(name = "STORE_ID", nullable = false, length = 10)
    private Integer storeId;

   /**
    * 会员等级ID 
    */ 
    @Column(name = "GRADE_ID", nullable = false, length = 10)
    private Integer gradeId;

   /**
    * 赠送积分 
    */ 
    @Column(name = "POINT", nullable = false, length = 10)
    private Integer point;

   /**
    * 卡券ID 
    */ 
    @Column(name = "COUPON_ID", nullable = false, length = 10)
    private Integer couponId;

   /**
    * 卡券数量 
    */ 
    @Column(name = "COUPON_NUM", nullable = false, length = 10)
    private Integer couponNum;

   /**
    * 创建时间 
    */ 
    @Column(name = "CREATE_TIME", nullable = false)
    private Date createTime;

   /**
    * 更新时间 
    */ 
    @Column(name = "UPDATE_TIME", nullable = false)
    private Date updateTime;

    /**
     * 状态
     */
    @Column(name = "STATUS", length = 1)
    private String status;

   /**
    * 最后操作人 
    */ 
    @Column(name = "OPERATOR", length = 30)
    private String operator;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
    this.storeId=storeId;
    }
    public Integer getGradeId(){
        return gradeId;
    }
    public void setGradeId(Integer graderId){
    this.gradeId=graderId;
    }
    public Integer getPoint(){
        return point;
    }
    public void setPoint(Integer point){
    this.point=point;
    }
    public Integer getCouponId(){
        return couponId;
    }
    public void setCouponId(Integer couponId){
    this.couponId=couponId;
    }
    public Integer getCouponNum(){
        return couponNum;
    }
    public void setCouponNum(Integer couponNum){
    this.couponNum=couponNum;
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
    public String getOperator(){
        return operator;
    }
    public void setOperator(String operator){
    this.operator=operator;
    }
}

