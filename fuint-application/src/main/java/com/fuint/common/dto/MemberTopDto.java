package com.fuint.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员排行DTO
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class MemberTopDto implements Serializable {

    /**
     * 会员ID
     */
    private Integer id;

    /**
     * 会员名称
     */
    private String name;

    /**
     * 会员号
     */
    private String userNo;

    /**
     * 消费金额
     */
    private BigDecimal amount;

    /**
     * 购买数量
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
    public String getUserNo(){
        return userNo;
    }
    public void setUserNo(String userNo){
        this.userNo=userNo;
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

