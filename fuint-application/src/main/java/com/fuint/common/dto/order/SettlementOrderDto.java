package com.fuint.common.dto.order;

import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 结算订单表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SettlementOrderDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="结算ID")
    private Integer settlementId;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="订单信息")
    private UserOrderDto orderInfo;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="备注说明")
    private String description;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A正常；D删除")
    private String status;

}
