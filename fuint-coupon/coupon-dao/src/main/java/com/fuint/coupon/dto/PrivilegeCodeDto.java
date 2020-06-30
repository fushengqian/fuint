package com.fuint.coupon.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description：发行码列表的DTO
 * @Author： changyouyi
 * @date:2017/5/16
 * @Copyright:the Corporation of mianshui365
 */
public class PrivilegeCodeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 券ID
     */
    private Long id;
    /**
     * 特权码规则名称
     */
    private String name;
    /**
     * 特权码有效开始时间
     */
    private Date startTime;
    /**
     * 特权码有效结束时间
     */
    private Date endTime;
    /**
     * 券码
     */
    private String code;
    /**
     * 推广方式
     */
    private String  type;
    /**
     * 状态
     */
    private String status;
    /**
     * 用户ID
     */
    private String userId;

    public PrivilegeCodeDto(Long id,Date startTime, Date endTime, String code,  String userId) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.code = code;
        this.userId = userId;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
