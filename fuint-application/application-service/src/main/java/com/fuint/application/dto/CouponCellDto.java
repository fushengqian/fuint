package com.fuint.application.dto;

import java.util.List;

public class CouponCellDto {

    private String mobile;

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
