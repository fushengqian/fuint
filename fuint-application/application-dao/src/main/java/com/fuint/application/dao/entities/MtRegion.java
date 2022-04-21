package com.fuint.application.dao.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

/**
 * mt_region 实体类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */ 
@Entity 
@Table(name = "mt_region")
public class MtRegion implements Serializable{
   /**
    * 区划ID
    */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 10)
    private Integer id;

   /**
    * 区划名称 
    */ 
    @Column(name = "NAME", nullable = false, length = 255)
    private String name;

   /**
    * 父级ID 
    */ 
    @Column(name = "PID", nullable = false, length = 10)
    private Integer pid;

   /**
    * 区划编码 
    */ 
    @Column(name = "CODE", nullable = false, length = 255)
    private String code;

   /**
    * 层级(1省级 2市级 3区/县级) 
    */ 
    @Column(name = "LEVEL", nullable = false, length = 3)
    private String level;

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
    public Integer getPid(){
        return pid;
    }
    public void setPid(Integer pid){
    this.pid=pid;
    }
    public String getCode(){
        return code;
    }
    public void setCode(String code){
    this.code=code;
    }
    public String getLevel(){
        return level;
    }
    public void setLevel(String level){
    this.level=level;
    }
}

