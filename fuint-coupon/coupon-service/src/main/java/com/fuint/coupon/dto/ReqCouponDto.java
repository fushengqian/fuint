package com.fuint.coupon.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券请求DTO
 * Created by zach on 2019/8/6.
 */
public class ReqCouponDto implements Serializable {
    /**
     * 券ID
     */
    private Long id;

    /**
     * 分组ID
     * */
    private Integer group_id;

    /**
     * 名称
     */
    private String name;

    /**
     * 有效期开始时间
     * */
    private Date begin_time;

    /**
     * 有效期结束时间
     * */
    private Date end_time;

    /**
     * 价值金额
     * */
    private BigDecimal money;

    /**
     * 发行数量
     * */
     private Integer total;

     /**
      * 例外时间
      * */
     private String excetp_time;

    /**
     * 店铺Id
     * */
    private String store_ids;

    /**
     * 后台备注
     * */
    private String remarks;

    /**
     * 图片
     * */
    private String image;

    /**
     * 备注
     */
    private String description;

    /**
     * 预存规则
     * */
    private String in_rule;

    /**
     * 核销规则
     * */
    private String out_rule;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 状态
     * */
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return group_id;
    }

    public void setGroupId(Integer group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBeginTime() {
        return begin_time;
    }

    public void setBeginTime(Date begin_time) {
        this.begin_time = begin_time;
    }

    public Date getEndTime() {
        return end_time;
    }

    public void setEndTime(Date end_time) {
        this.end_time = end_time;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getExceptTime() {
        return excetp_time;
    }

    public void setExceptTime(String excetp_time) {
        this.excetp_time = excetp_time;
    }

    public String getStoreIds() {
        return store_ids;
    }

    public void setStoreIds(String store_ids) {
        this.store_ids = store_ids;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInRule() {
        return in_rule;
    }

    public void setInRule(String in_rule) {
        this.in_rule = in_rule;
    }

    public String getOutRule() {
        return out_rule;
    }

    public void setOutRule(String out_rule) {
        this.out_rule = out_rule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) { this.status = status;}
}
