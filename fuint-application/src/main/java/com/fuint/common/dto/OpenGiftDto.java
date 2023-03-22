package com.fuint.common.dto;

import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUserGrade;
import java.io.Serializable;

/**
 * 开卡赠礼实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class OpenGiftDto implements Serializable {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * 门店信息
     */
    private MtStore storeInfo;

    /**
     * 会员等级信息
     */
    private MtUserGrade gradeInfo;

    /**
     * 赠送积分
     */
    private Integer point;

    /**
     * 卡券信息
     */
    private MtCoupon couponInfo;

    /**
     * 卡券数量
     */
    private Integer couponNum;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 最后操作人
     */
    private String operator;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MtStore getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(MtStore storeInfo) {
        this.storeInfo = storeInfo;
    }

    public MtUserGrade getGradeInfo() {
        return gradeInfo;
    }

    public void setGradeInfo(MtUserGrade gradeInfo) {
        this.gradeInfo = gradeInfo;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public MtCoupon getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(MtCoupon couponInfo) {
        this.couponInfo = couponInfo;
    }

    public Integer getCouponNum() {
        return couponNum;
    }

    public void setCouponNum(Integer couponNum) {
        this.couponNum = couponNum;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}

