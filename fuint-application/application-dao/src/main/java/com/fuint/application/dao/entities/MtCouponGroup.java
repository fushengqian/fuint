package com.fuint.application.dao.entities;

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
    * mt_coupon_group 实体类
    * Created by zach
    * Wed Aug 28 13:51:33 GMT+08:00 2019
    */ 
@Entity 
@Table(name = "mt_coupon_group")
public class MtCouponGroup implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 分组名称
    */ 
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

   /**
    * 价值金额 
    */ 
    @Column(name = "MONEY")
    private BigDecimal money;

   /**
    * 种类数量
    */
   @Column(name = "NUM", length = 10)
   private Integer num;

   /**
    * 发行数量 
    */ 
    @Column(name = "TOTAL", length = 10)
    private Integer total;

   /**
    * 备注 
    */ 
    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

   /**
    * 创建日期 
    */ 
    @Column(name = "CREATE_TIME")
    private Date createTime;

   /**
    * 更新日期 
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
    public String getName(){
        return name;
    }
    public void setName(String name){
    this.name=name;
    }
    public BigDecimal getMoney(){
        return money;
    }
    public void setMoney(BigDecimal money){
    this.money=money;
    }
    public Integer getNum(){
           return num;
       }
    public void setNum(Integer num){
           this.num=num;
       }
    public Integer getTotal(){
        return total;
    }
    public void setTotal(Integer total){
    this.total=total;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
    this.description=description;
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

