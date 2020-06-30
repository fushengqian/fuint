package com.fuint.coupon.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 促销活动
 * Created by changyouyi on 2017/2/17.
 */
public class ActivityDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 活动主键ID
     */
    private Long id;
    /**
     * 促销活动名称
     */
    private String name;

    /**
     * 规则编码
     */
    private String code;

    /**
     * 规则状态(A:有效 D:无效)
     */
    private String status;

    /**
     * 促销开始时间
     */
    private Date beginTime;
    /**
     * 促销结束时间
     */
    private Date endTime;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 规则权重
     */
    private int weight;
    /**
     * 规则类型（1 限时折扣、 2 每满减、3 满额减、4 满件折、5 优惠券）
     */
    private String type;

    /**
     * 外键 促销范围ID
     */
    private Long rangeId;
    /**
     * 促销范围编码
     */
    private String rangeCode;
    /**
     * 外键 促销范围名称
     */
    private String rangeName;
    /**
     * 外键 促销特例规则ID
     */
    private Long specialId;
    /**
     * 剔除范围编码
     */
    private String specialCode;
    /**
     * 外键 促销特例规则名称
     */
    private String specialName;
    /**
     * 外键 促销规则ID
     */
    private String ruleIds;
    /**
     * 促销规则编码
     */
    private String ruleCode;
    /**
     * 外键 促销规则名称
     */
    private String ruleName;
    /**
     * 操作人
     */
    private String operator;

    /**
     * 宣传语
     */
    private String advertisement;

    public ActivityDto() {
    }

    public ActivityDto(Long id, String name, String code, String status, Date beginTime, Date endTime, Date createTime,
                       int weight, String type, String operator, String advertisement) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.status = status;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.weight = weight;
        this.type = type;
        this.operator = operator;
        this.advertisement = advertisement;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getRangeId() {
        return rangeId;
    }

    public void setRangeId(Long rangeId) {
        this.rangeId = rangeId;
    }

    public Long getSpecialId() {
        return specialId;
    }

    public void setSpecialId(Long specialId) {
        this.specialId = specialId;
    }

    public String getRangeName() {
        return rangeName;
    }

    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }

    public String getSpecialName() {
        return specialName;
    }

    public void setSpecialName(String specialName) {
        this.specialName = specialName;
    }

    public String getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(String ruleIds) {
        this.ruleIds = ruleIds;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(String advertisement) {
        this.advertisement = advertisement;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getSpecialCode() {
        return specialCode;
    }

    public void setSpecialCode(String specialCode) {
        this.specialCode = specialCode;
    }

    public String getRangeCode() {
        return rangeCode;
    }

    public void setRangeCode(String rangeCode) {
        this.rangeCode = rangeCode;
    }
}
