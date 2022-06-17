package com.fuint.application.dto;

/**
 * RechargeRuleDto 充值规则实体类
 * Created by FSQ
 * Contact wx fsq_better
 */
public class RechargeRuleDto {

    private String rechargeAmount;
    private String giveAmount;

    public String getRechargeAmount() {
        return rechargeAmount;
    }
    public void setRechargeAmount(String rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getGiveAmount() {
        return giveAmount;
    }
    public void setGiveAmount(String giveAmount) {
        this.giveAmount = giveAmount;
    }
}
