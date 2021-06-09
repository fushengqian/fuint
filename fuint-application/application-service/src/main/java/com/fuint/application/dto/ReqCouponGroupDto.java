package com.fuint.application.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 优惠分组请求DTO
 * Created by zach on 2019/7/20.
 */
public class ReqCouponGroupDto implements Serializable {
    /**
     * ID
     */
    private Long id;
    /**
     * 组名称
     */
    private String name;

    /**
     * 价值金额
     * */
    private BigDecimal money;

    /**
     * 发行数量
     * */
     private Integer total;

    /**
     * 分组描述
     */
    private String description;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 状态
     * */
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
