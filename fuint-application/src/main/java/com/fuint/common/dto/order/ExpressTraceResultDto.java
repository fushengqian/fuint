package com.fuint.common.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 物流查询结果
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ExpressTraceResultDto implements Serializable {

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="物流公司名称")
    private String expressCompany;

    @Schema(description="物流公司编码")
    private String expressCode;

    @Schema(description="物流单号")
    private String expressNo;

    @Schema(description="发货时间")
    private String expressTime;

    @Schema(description="物流状态编码")
    private String state;

    @Schema(description="物流状态说明")
    private String stateText;

    @Schema(description="是否已签收")
    private Boolean isCheck;

    @Schema(description="物流轨迹列表")
    private List<ExpressTraceDto> traceList;
}
