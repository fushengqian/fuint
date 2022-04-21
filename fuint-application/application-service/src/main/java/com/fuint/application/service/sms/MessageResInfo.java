package com.fuint.application.service.sms;

/**
 * 短信发送返回
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
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
