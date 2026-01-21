package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员余额实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user_balance")
@ApiModel(value = "user_balance表对象", description = "user_balance表对象")
public class MtUserBalance implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("生成时间")
    private Date createTime;

    @ApiModelProperty("过期时间")
    private Date expired;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
