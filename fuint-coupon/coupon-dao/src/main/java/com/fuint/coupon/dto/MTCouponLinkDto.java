package com.fuint.coupon.dto;

import java.util.Date;

/**
 * @Description;
 * @Author: lujia
 * @Date:2017/7/26
 * @Copyright: Corporation of mianshui365
 */
public class MTCouponLinkDto {

    /***
     * 券ID
     */
    private String couponId;
    /***
     * 券码
     */
    private String couponCode;

    /***
     * 优惠券活动id
     */
    private String activityId;
    /***
     * 优惠券活动名称
      */
    private String couponActivityName;

    /***
     * 优惠券状态  0:未领取； 1：已领取未使用；2：已冻结；3：已使用；4：已结算；9：已作废
     */
    private String status;

    /***
     * 优惠券链接
     */
    private String couponLink;

    /***
     * 用户
     */
    private String userId;

    /***
     * 领取链接类型（P：PC；M：移动端）
     */
    private String receiveLinkType;

    /***
     * 优惠券结束时间
     */
    private Date endTime;

    /***
     * 优惠券活动Code
     */
    private String couponInfoCode;

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getCouponActivityName() {
        return couponActivityName;
    }

    public void setCouponActivityName(String couponActivityName) {
        this.couponActivityName = couponActivityName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCouponLink() {
        return couponLink;
    }

    public void setCouponLink(String couponLink) {
        this.couponLink = couponLink;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReceiveLinkType() {
        return receiveLinkType;
    }

    public void setReceiveLinkType(String receiveLinkType) {
        this.receiveLinkType = receiveLinkType;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCouponInfoCode() {
        return couponInfoCode;
    }

    public void setCouponInfoCode(String couponInfoCode) {
        this.couponInfoCode = couponInfoCode;
    }
}
