package com.fuint.common.dto.order;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("物流公司名称")
    private String expressCompany;

    @ApiModelProperty("物流公司编码")
    private String expressCode;

    @ApiModelProperty("物流单号")
    private String expressNo;

    @ApiModelProperty("发货时间")
    private String expressTime;

    @ApiModelProperty("物流状态编码")
    private String state;

    @ApiModelProperty("物流状态说明")
    private String stateText;

    @ApiModelProperty("是否已签收")
    private Boolean isCheck;

    @ApiModelProperty("物流轨迹列表")
    private List<ExpressTraceDto> traceList;
}
