package com.fuint.coupon.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CouponTotalDto {

    /**
     * 发券量
     */
    @Transient
    private Long couponTotal;
    /**
     * 未使用量
     */
    @Transient
    private Long unUsedTotal;
    /**
     * 使用量
     */
    @Transient
    private Long usedTotal;

    /**
     * 过期量
     */
    @Transient
    private Long expireTotal;

    /**
     * 作废量
     */
    @Transient
    private Long disableTotal;


    public Long getCouponTotal() {
        return couponTotal;
    }

    public void setCouponTotal(Long couponTotal) {
        this.couponTotal = couponTotal;
    }

    public Long getUnUsedTotal() {
        return unUsedTotal;
    }

    public void setUnUsedTotal(Long unUsedTotal) {
        this.unUsedTotal = unUsedTotal;
    }

    public Long getUsedTotal() {
        return usedTotal;
    }

    public void setUsedTotal(Long usedTotal) {
        this.usedTotal = usedTotal;
    }

    public Long getExpireTotal() {
        return expireTotal;
    }

    public void setExpireTotal(Long expireTotal) {
        this.expireTotal = expireTotal;
    }

    public Long getDisableTotal() {
        return disableTotal;
    }

    public void setDisableTotal(Long disableTotal) {
        this.disableTotal = disableTotal;
    }
}

