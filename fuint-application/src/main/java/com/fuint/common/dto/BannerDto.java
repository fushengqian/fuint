package com.fuint.common.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * BannerDto 实体类
 * Created by FSQ
 */
public class BannerDto implements Serializable {
   /**
    * 自增ID 
    */
    private Integer id;

   /**
    * 标题 
    */
    private String title;

    /**
     * 所属店铺
     * */
    private Integer storeId;

    /**
     * 链接地址
     */
    private String url;

   /**
    * 图片地址 
    */
    private String image;

   /**
    * 描述 
    */
    private String description;

   /**
    * 创建时间 
    */
    private Date createTime;

   /**
    * 更新时间 
    */
    private Date updateTime;

   /**
    * 最后操作人 
    */
    private String operator;

   /**
    * A：正常；D：删除 
    */
    private String status;

    /**
     * 排序
     * */
    private Integer sort;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
    this.title=title;
    }
    public Integer getStoreId(){
        return storeId;
    }
    public void setStoreId(Integer storeId){
        this.storeId=storeId;
    }
    public String getUrl(){
        return url;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public String getImage(){
        return image;
    }
    public void setImage(String image){
    this.image=image;
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
    public Integer getSort(){
        return sort;
    }
    public void setSort(Integer sort){
        this.sort=sort;
    }
}

