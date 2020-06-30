package com.fuint.coupon.dto;

/**
 * @Description;
 * @Author: lujia
 * @Date:2017/7/26
 * @Copyright: Corporation of mianshui365
 */
public class MTActivityCouponFixedDto {

    /***
     * 券ID
     */
    private String couponId;

    /***
     * 优惠券活动Code
     */
    private String activityCode;
    /***
     * 优惠券活动名称
      */
    private String activityName;

    /***
     * 券码
     */
    private String couponCode;


    /***
     * 兑换码
     */
    private String exchangeCode;

    /***
     * 导出状态（0：未导出；1：已导出）
     */
    private String exportStatus;


    /***
     * 优惠券状态  0:未领取； 1：已领取未使用；2：已冻结；3：已使用；4：已结算；9：已作废
     */
    private String status;

    /***
     * 获取来源 （0：用户领取；1：系统发放；2：人工发放；3：口令兑换；4：秒杀支付）
     */
    private String receiveType;

    /***
     * 用户
     */
    private String userId;

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getExportStatus() {
        return exportStatus;
    }

    public void setExportStatus(String exportStatus) {
        this.exportStatus = exportStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
