package com.fuint.coupon.service.sms;

/**
 * @Description:
 * @Author: chenggang
 * @date: 2017/8/23
 * @Copyright:the Corporation of mianshui365
 */
public class MessageResInfo {
    private String[] sendIds;
    private Boolean result;
    private String[] smsId;



    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String[] getSendIds() {
        return sendIds;
    }

    public void setSendIds(String[] sendIds) {
        this.sendIds = sendIds;
    }

    public String[] getSmsId() {
        return smsId;
    }

    public void setSmsId(String[] smsId) {
        this.smsId = smsId;
    }
}
