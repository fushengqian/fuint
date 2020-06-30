package com.fuint.coupon.dto;

import com.fuint.base.dao.entities.TAccount;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 促销活动规则
 * Created by changyouyi on 2017/2/13.
 */
public class ActivityRuleDto implements Serializable{


    private static final long serialVersionUID = -64645783493544891L;
    /**
     * 主键ID
     */
    private String id;
    /**
     * 规则名称
     */
    private String name;
    /**
     * 规则编码
     */
    private String code;
    /**
     * 规则描述
     */
    private String description;
    /**
     * 规则状态(A:有效 D:无效)
     */
    private String status;
    /**
     * 规则类型（1 限时折扣、 2 每满减、3 满额减、4 满件折、5 优惠券）
     */
    private String ruleType;

    /**
     * 规则内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 一个促销规则可以关联多个活动
     */
    private List<ActivityDto> activityDtos;
    /**
     * 操作人
     */
    private TAccount operator;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public TAccount getOperator() {
        return operator;
    }

    public void setOperator(TAccount operator) {
        this.operator = operator;
    }

    public List<ActivityDto> getActivityDtos() {
        return activityDtos;
    }

    public void setActivityDtos(List<ActivityDto> activityDtos) {
        this.activityDtos = activityDtos;
    }
}
