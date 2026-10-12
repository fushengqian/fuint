package com.fuint.common.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fuint.framework.pagination.PaginationResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 结算实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SettlementDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="结算单号")
    private String settlementNo;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="订单总金额")
    private BigDecimal totalOrderAmount;

    @Schema(description="结算金额")
    private BigDecimal amount;

    @Schema(description="结算订单")
    private PaginationResponse<SettlementOrderDto> orderList;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="备注说明")
    private String description;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="支付状态，A待支付；B已支付")
    private String payStatus;

    @Schema(description="状态，A正常；D删除")
    private String status;

}
