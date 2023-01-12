package com.fuint.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 卡券分组请求DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class ReqCouponGroupDto implements Serializable {
    /**
     * ID
     */
    private Integer id;
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
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
