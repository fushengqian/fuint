package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券DTO
 * Created by zach on 2021/4/22.
 * Updated by zach on 2021/5/1.
 */
public class CouponDto implements Serializable {

    /**
     * 自增ID
     * */
    private Integer id;

    /**
     * 名称（券名称）
     * */
    private String name;

    /**
     * 卡券类型
     * */
    private String type;

    /**
     * 状态
     * */
    private String status;

    /**
     * 获取规则
     * */
    private String inRule;

    /**
     * 使用规则
     * */
    private String outRule;

    /**
     * 图片
     * */
    private String image;

    /**
     * 面额
     * */
    private BigDecimal amount;

    /**
     * 卖点
     * */
    private String sellingPoint;

    /**
     * 已领取、预存张数
     * */
    private Integer gotNum;

    /**
     * 限制数量
     * */
    private Integer limitNum;

    /**
     * 是否领取
     * */
    private boolean isReceive;

    /**
     * 有效期
     * */
    private String effectiveDate;

    /**
     * 卡券说明
     * */
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getInRule() {
        return inRule;
    }
    public void setInRule(String inRule) {
        this.inRule = inRule;
    }

    public String getOutRule() {
        return outRule;
    }
    public void setOutRule(String outRule) {
        this.outRule = outRule;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSellingPoint() {
        return sellingPoint;
    }
    public void setSellingPoint(String sellingPoint) {
        this.sellingPoint = sellingPoint;
    }

    public Integer getLimitNum() {
        return limitNum;
    }
    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getGotNum() {
        return gotNum;
    }
    public void setGotNum(Integer gotNum) {
        this.gotNum = gotNum;
    }

    public boolean getIsReceive() {
        return isReceive;
    }
    public void setIsReceive(boolean isReceive) {
        this.isReceive = isReceive;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
