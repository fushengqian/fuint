package com.fuint.common.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 核销卡券流水dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ConfirmLogDto {

    /**
     * 自增ID
     */
     private Integer id;

    /**
     * 核销编码
     */
    private String code;

    /**
     * 核销状态
     */
    private String status;

    /**
     * 会员卡券ID
     */
    private Integer userCouponId;

    /**
     * 卡券信息
     */
    private MtCoupon couponInfo;

    /**
     * 核销会员信息
     */
    private MtUser userInfo;

    /**
     * 使用店铺信息
     */
    private MtStore storeInfo;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 核销面额
     */
    private BigDecimal amount;

    /**
     * 核销uuid
     */
    private String uuid;

    /**
     * 核销备注
     */
    private String remark;

    /**
     * 最后操作人
     */
    private String operator;

    public Integer getId(){
    return id;
    }
    public void setId(Integer id){
    this.id=id;
    }
    public String getCode(){
    return code;
    }
    public void setCode(String code){
    this.code=code;
    }
    public Integer getUserCouponId(){
    return userCouponId;
    }
    public void setUserCouponId(Integer userCouponId){
    this.userCouponId=userCouponId;
    }
    public MtCoupon getCouponInfo(){
    return couponInfo;
    }
    public void setCouponInfo(MtCoupon couponInfo){
    this.couponInfo=couponInfo;
    }
    public MtUser getUserInfo(){
     return userInfo;
    }
    public void setUserInfo(MtUser userInfo){
    this.userInfo=userInfo;
    }
    public MtStore getStoreInfo(){
     return storeInfo;
    }
    public void setStoreInfo(MtStore storeInfo){
    this.storeInfo=storeInfo;
    }
    public Date getCreateTime(){
     return createTime;
    }
    public void setCreateTime(Date createTime){
    this.createTime=createTime;
    }
    public Date getUpdateTime(){
     return updateTime;
    }
    public void setUpdateTime(Date updateTime){
    this.updateTime=updateTime;
    }
    public BigDecimal getAmount(){
     return amount;
    }
    public void setAmount(BigDecimal amount){
    this.amount=amount;
    }
    public String getUuid(){
    return uuid;
    }
    public void setUuid(String uuid){
    this.uuid=uuid;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public String getRemark(){
        return remark;
    }
    public void setRemark(String remark){
        this.remark=remark;
    }
    public String getOperator(){
        return operator;
    }
    public void setOperator(String operator){
        this.operator=operator;
    }
}

