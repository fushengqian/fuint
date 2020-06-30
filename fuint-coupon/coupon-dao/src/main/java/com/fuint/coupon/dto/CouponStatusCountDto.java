package com.fuint.coupon.dto;

import java.io.Serializable;

/**
 * 优惠券状态统计实体
 * Created by zach on 2017/7/26.
 */
public class CouponStatusCountDto implements Serializable {
    /**
     * 优惠券状态
     */
    private String status;
    /**
     * 优惠券数量
     */
    private Integer codeNum;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCodeNum() {
        return codeNum;
    }

    public void setCodeNum(Integer codeNum) {
        this.codeNum = codeNum;
    }
}
