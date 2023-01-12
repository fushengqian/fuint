package com.fuint.common.dto;

import com.fuint.repository.model.MtUser;
import java.math.BigDecimal;

/**
 * 收银挂单实体类
 */
public class HangUpDto {

    /**
     * 挂单号
     */
    private String hangNo;

    /**
     * 是否空白
     * */
    private boolean isEmpty;

    /**
     * 会员信息
     */
    private MtUser memberInfo;

    /**
     * 件数
     */
    private Integer num;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 时间
     */
    private String dateTime;

    public String getHangNo() {
        return hangNo;
    }
    public void setHangNo(String hangNo) {
        this.hangNo = hangNo;
    }

    public boolean getIsEmpty() {
        return isEmpty;
    }
    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public MtUser getMemberInfo() {
        return memberInfo;
    }
    public void setMemberInfo(MtUser memberInfo) {
        this.memberInfo = memberInfo;
    }

    public BigDecimal getAmount(){
        return amount;
    }
    public void setAmount(BigDecimal amount){
        this.amount=amount;
    }

    public Integer getNum(){
        return num;
    }
    public void setNum(Integer num){
        this.num=num;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
