package com.fuint.common.dto;

/**
 * 预存规则实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class PreStoreRuleDto {

    private String preStoreAmount;
    private String targetAmount;

    public String getPreStoreAmount() {
        return preStoreAmount;
    }
    public void setPreStoreAmount(String preStoreAmount) {
        this.preStoreAmount = preStoreAmount;
    }

    public String getTargetAmount() {
        return targetAmount;
    }
    public void setTargetAmount(String targetAmount) {
        this.targetAmount = targetAmount;
    }
}
