package com.fuint.common.dto;

/**
 * 充值规则实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
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
