package com.fuint.common.dto;

import java.util.List;

public class CouponCellDto {

    private String mobile;

    // 商户ID
    private Integer merchantId;

    // 分组ID
    private List<Integer> groupId;

    // 发放数量
    private List<Integer> num;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public List<Integer> getGroupId() {
        return groupId;
    }

    public void setGroupId(List<Integer> groupId) {
        this.groupId = groupId;
    }

    public List<Integer> getNum() {
        return num;
    }

    public void setNum(List<Integer> num) {
        this.num = num;
    }
}
