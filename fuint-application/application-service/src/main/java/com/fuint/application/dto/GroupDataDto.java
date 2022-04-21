package com.fuint.application.dto;

public class GroupDataDto {
    private Integer sendNum;

    private Integer unSendNum;

    private Integer useNum;

    private Integer expireNum;

    private Integer cancelNum;

    public Integer getSendNum() { return sendNum; }
    public void setSendNum(Integer sendNum) { this.sendNum = sendNum; }

    public Integer getUnSendNum() {
        return unSendNum;
    }
    public void setUnSendNum(Integer unSendNum) {
        this.unSendNum = unSendNum;
    }

    public Integer getUseNum() {
        return useNum;
    }
    public void setUseNum(Integer useNum) {
        this.useNum = useNum;
    }

    public Integer getExpireNum() {
        return expireNum;
    }
    public void setExpireNum(Integer expireNum) {
        this.expireNum = expireNum;
    }

    public Integer getCancelNum() {
        return cancelNum;
    }
    public void setCancelNum(Integer cancelNum) {
        this.cancelNum = cancelNum;
    }
}
