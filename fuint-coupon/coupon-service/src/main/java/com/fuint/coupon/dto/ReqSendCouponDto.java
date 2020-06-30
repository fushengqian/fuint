package com.fuint.coupon.dto;

import java.io.Serializable;

/**
 * 发放优惠券请求DTO
 * Created by zach on 2019/8/28.
 */
public class ReqSendCouponDto implements Serializable {

    /**
     * 分组ID
     * */
    private Integer group_id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 发放数量
     */
    private Integer num;

    public Integer getGroupId() {
        return group_id;
    }

    public void setGroupId(Integer group_id) {
        this.group_id = group_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
