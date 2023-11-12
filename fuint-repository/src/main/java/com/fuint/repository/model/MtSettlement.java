package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 结算表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_settlement")
@ApiModel(value = "MtSettlement对象", description = "结算表")
public class MtSettlement implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("订单总金额")
    private BigDecimal totalOrderAmount;

    @ApiModelProperty("结算金额")
    private BigDecimal amount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("备注说明")
    private String description;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("支付状态，A待支付；B已支付")
    private String payStatus;

    @ApiModelProperty("状态，A正常；D删除")
    private String status;

}
