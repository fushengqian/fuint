package com.fuint.common.dto;

import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 我的卡券DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class MyCouponDto implements Serializable {

    /**
     * 自增ID
     * */
    private Integer id;

    /**
     * 卡券名称
     * */
    private String name;

    /**
     * 核销编码
     */
    private String code;

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
     * 图片
     * */
    private String image;

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
     * 使用时间
     * */
    private Date usedTime;

    /**
     * 领券时间
     * */
    private Date createTime;

    /**
     * 会员信息
     * */
    private MtUser userInfo;

    /**
     * 使用店铺
     * */
    private MtStore storeInfo;

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

    public String getCode(){
        return code;
    }
    public void setCode(String code){
        this.code=code;
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

    public Date getUsedTime(){
        return usedTime;
    }
    public void setUsedTime(Date usedTime){
        this.usedTime=usedTime;
    }

    public Date getCreateTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime=createTime;
    }

    public MtUser getUserInfo() {
        return userInfo;
    }
    public void setUserInfo(MtUser userInfo) {
        this.userInfo = userInfo;
    }

    public MtStore getStoreInfo() {
        return storeInfo;
    }
    public void setStoreInfo(MtStore storeInfo) {
        this.storeInfo = storeInfo;
    }
}
