package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

/**
 * mt_goods_spec 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */ 
@Entity 
@Table(name = "mt_goods_spec")
public class MtGoodsSpec implements Serializable{
   /**
    * 自增ID 
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 商品ID 
    */ 
    @Column(name = "GOODS_ID", nullable = false, length = 10)
    private Integer goodsId;

   /**
    * 规格名称 
    */ 
    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

   /**
    * 规格值 
    */ 
    @Column(name = "VALUE", nullable = false, length = 100)
    private String value;

    /**
     * 状态
     */
    @Column(name = "STATUS", length = 1)
    private String status;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public Integer getGoodsId(){
        return goodsId;
    }
    public void setGoodsId(Integer goodsId){
    this.goodsId=goodsId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
    this.name=name;
    }
    public String getValue(){
        return value;
    }
    public void setValue(String value){
    this.value=value;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
}

