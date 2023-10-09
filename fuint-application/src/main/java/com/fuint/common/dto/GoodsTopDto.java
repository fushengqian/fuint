package com.fuint.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品排行DTO
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class GoodsTopDto implements Serializable {

    /**
     * 商品ID
     */
    private Integer id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品条码
     */
    private String goodsNo;

    /**
     * 销售金额
     */
    private BigDecimal amount;

    /**
     * 销售数量
     */
    private Integer num;

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getGoodsNo(){
        return goodsNo;
    }
    public void setGoodsNo(String goodsNo){
        this.goodsNo=goodsNo;
    }
    public Integer getNum(){
        return num;
    }
    public void setNum(Integer num){
        this.num=num;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public void setAmount(BigDecimal amount){
        this.amount=amount;
    }
}

