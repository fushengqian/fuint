package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 结算订单表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_settlement_order")
@ApiModel(value = "MtSettlementOrder对象", description = "结算订单表")
public class MtSettlementOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("结算ID")
    private Integer settlementId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注说明")
    private String description;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态，A正常；D删除")
    private String status;

}
