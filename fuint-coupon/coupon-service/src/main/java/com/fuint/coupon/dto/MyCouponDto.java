package com.fuint.coupon.dto;

import com.fuint.coupon.dao.entities.MtCoupon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 我的优惠券DTO
 * Created by zach on 2019/8/28.
 */
public class MyCouponDto implements Serializable {

    /**
     * 自增ID
     * */
    private Integer id;

    /**
     * 名称（券名称）
     * */
    private String name;

    /**
     * 状态
     * */
    private String status;

    /**
     * 券ID
     * */
    private Integer couponId;

    /**
     * 使用规则
     * */
    private String useRule;

    /**
     * 图片
     * */
    private String image;

    /**
     * 价值
     * */
    private BigDecimal money;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getUseRule() {
        return useRule;
    }
    public void setUseRule(String useRule) {
        this.useRule = useRule;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getMoney() {
        return money;
    }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
