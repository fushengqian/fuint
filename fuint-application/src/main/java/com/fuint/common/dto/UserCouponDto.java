package com.fuint.common.dto;

import com.fuint.repository.model.MtConfirmLog;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 我的卡券实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
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
     * 核销编码
     * */
    private String code;

    /**
     * 二维码
     * */
    private String qrCode;

    /**
     * 图片
     * */
    private String image;

    /**
     * 面额
     * */
    private BigDecimal amount;

    /**
     * 是否允许转赠
     * */
    private boolean isGive;

    /**
     * 余额
     * */
    private BigDecimal balance;

    /**
     * 核销次数
     * */
    private Integer confirmCount;

    /**
     * 核销记录
     * */
    private List<MtConfirmLog> confirmLogs;

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

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getIsGive() {
        return isGive;
    }
    public void setIsGive(Boolean isGive) {
        this.isGive = isGive;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getConfirmCount() {
        return confirmCount;
    }
    public void setConfirmCount(Integer confirmCount) {
        this.confirmCount = confirmCount;
    }

    public List<MtConfirmLog> getConfirmLogs() {
        return confirmLogs;
    }
    public void setConfirmLogs(List<MtConfirmLog> confirmLogs) {
        this.confirmLogs = confirmLogs;
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
