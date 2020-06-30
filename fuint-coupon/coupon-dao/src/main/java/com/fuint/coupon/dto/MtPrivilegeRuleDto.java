package com.fuint.coupon.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fuint.coupon.util.JsonDateDeserializer;


import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: chenggang
 * @date: 2017/5/10
 * @Copyright:the Corporation of mianshui365
 */
public class MtPrivilegeRuleDto implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 活动ID
     */
    private String[] activityId;

    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 特权码名称（用于前端展示）
     */
    private String codeName;

    /**
     * 特权码规则编码（保证唯一）
     */
    private String code;

    /**
     * 状态(A:有效 D:无效)
     */
    private String status;

    /**
     * 规则权重（优先级）（数值小的优先级高）
     */
    private int weight;

    /**
     * 有效期类型（0：绝对时间；1：相对时间）
     */
    private String timeType;


    /**
     * 开始时间（TIME_TYPE = 0 时不能为空）
     */
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date startTime;

    /**
     * 结束时间（TIME_TYPE = 0 时不能为空）
     */
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date endTime;


    /**
     * 有效天数（TIME_TYPE = 1 时不能为空
     */
    private Integer effectiveDays;

    /**
     * 优惠说明
     */
    private String discountDesc;

    /**
     * 详细说明
     */
    private String description;

    /**
     * 创建时间
     */
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date createTime;
    /**
     * 修改时间
     */
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date updateTime;
    /**
     * 操作人
     */
    private String operator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[] getActivityId() {
        return activityId;
    }

    public void setActivityId(String[] activityId) {
        this.activityId = activityId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getEffectiveDays() {
        return effectiveDays;
    }

    public void setEffectiveDays(Integer effectiveDays) {
        this.effectiveDays = effectiveDays;
    }

    public String getDiscountDesc() {
        return discountDesc;
    }

    public void setDiscountDesc(String discountDesc) {
        this.discountDesc = discountDesc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
}
