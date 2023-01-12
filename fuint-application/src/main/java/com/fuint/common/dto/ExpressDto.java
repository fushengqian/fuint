package com.fuint.common.dto;

/**
 * 订单物流信息dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ExpressDto {
    private String expressCompany;
    private String expressNo;
    private String expressTime;

    public String getExpressCompany(){
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany){
        this.expressCompany = expressCompany;
    }

    public String getExpressNo(){
        return expressNo;
    }

    public void setExpressNo(String expressNo){
        this.expressNo = expressNo;
    }

    public String getExpressTime(){
        return expressTime;
    }

    public void setExpressTime(String expressTime){
        this.expressTime = expressTime;
    }
}
