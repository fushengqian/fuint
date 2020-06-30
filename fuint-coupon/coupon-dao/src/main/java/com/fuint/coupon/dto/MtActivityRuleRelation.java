package com.fuint.coupon.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 促销活动规则的关系
 * <p>
 * Created by hanxiaoqiang on 2017/7/7.
 */
public class MtActivityRuleRelation implements Serializable {
    /**
     * 规则ID: 促销范围规则 剔除范围规则
     */
    private int ruleId;
    /**
     * 规则适用的促销活动
     */
    private String activityCode;
    /**
     * 促销活动开始时间
     */
    private Date startTime;
    /**
     * 促销活动结束时间
     */
    private Date endTime;

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
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
}
