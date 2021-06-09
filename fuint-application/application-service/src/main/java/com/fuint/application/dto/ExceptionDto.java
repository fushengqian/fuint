package com.fuint.application.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 特例
 * Created by zach on 2019/3/16.
 */
public class ExceptionDto implements Serializable {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 状态(A:有效 D:无效)
     */
    private String status;
    /**
     * 平台
     */
    private String[] platformId;

    /**
     * 渠道
     */
    private String[] shopId;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 一级分类
     */
    private String[] firstCategoryId;
    /**
     * 一级分类
     */
    private String firstCategory;

    /**
     * 二级分类
     */
    private String[] secondCategoryId;
    /**
     * 二级分类
     */
    private String secondCategory;

    /**
     * 三级分类
     */
    private String[] thirdCategoryId;
    /**
     * 三级分类
     */
    private String thirdCategory;

    /**
     * 商品SKU（多个值使用英文逗号隔开）
     */
    private String skus;
    /**
     * 标签类型
     */
    private String tagType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 操作人
     */
    private String operator;

    public ExceptionDto() {
    }

    public ExceptionDto(Long id, String name, String description, String status, String skus, String tagType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.skus = skus;
        this.tagType = tagType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String[] platformId) {
        this.platformId = platformId;
    }

    public String[] getShopId() {
        return shopId;
    }

    public void setShopId(String[] shopId) {
        this.shopId = shopId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public String[] getFirstCategoryId() {
        return firstCategoryId;
    }

    public void setFirstCategoryId(String[] firstCategoryId) {
        this.firstCategoryId = firstCategoryId;
    }

    public String getFirstCategory() {
        return firstCategory;
    }

    public void setFirstCategory(String firstCategory) {
        this.firstCategory = firstCategory;
    }

    public String[] getSecondCategoryId() {
        return secondCategoryId;
    }

    public void setSecondCategoryId(String[] secondCategoryId) {
        this.secondCategoryId = secondCategoryId;
    }

    public String getSecondCategory() {
        return secondCategory;
    }

    public void setSecondCategory(String secondCategory) {
        this.secondCategory = secondCategory;
    }

    public String[] getThirdCategoryId() {
        return thirdCategoryId;
    }

    public void setThirdCategoryId(String[] thirdCategoryId) {
        this.thirdCategoryId = thirdCategoryId;
    }

    public String getThirdCategory() {
        return thirdCategory;
    }

    public void setThirdCategory(String thirdCategory) {
        this.thirdCategory = thirdCategory;
    }

    public String getSkus() {
        return skus;
    }

    public void setSkus(String skus) {
        this.skus = skus;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }
}
