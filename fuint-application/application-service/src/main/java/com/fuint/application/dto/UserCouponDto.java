package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 我的卡券DTO
 * Created by zach on 2021/04/29.
 */
public class UserCouponDto implements Serializable {

    /**
     * 自增ID
     * */
    private Integer id;

    /**
     * 卡券名称
     * */
    private String name;

    /**
     * 卡券类型
     * */
    private String type;

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
     * 编码
     * */
    private String code;

    /**
     * 二维码
     * */
    private String qrCode;

    /**
     * 面额
     * */
    private BigDecimal amount;

    /**
     * 余额
     * */
    private BigDecimal balance;

    /**
     * 是否可以使用(过期、状态等)
     * */
    private boolean canUse;

    /**
     * 有效期
     * */
    private String effectiveDate;

    /**
     * 小提示
     * */
    private String tips;

    /**
     * 描述信息
     * */
    private String description;

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

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getQrCode() {
        return qrCode;
    }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean getCanUse() {
        return canUse;
    }
    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getTips() {
        return tips;
    }
    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
