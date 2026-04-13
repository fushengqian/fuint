package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 售后订单信息请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RefundInfoParam extends PageParam implements Serializable {

    @ApiModelProperty(value="售后单ID", name="refundId")
    private Integer refundId;

    @ApiModelProperty(value="售后状态", name="status")
    private String status;

    @ApiModelProperty(value="备注信息", name="remark")
    private String remark;

    @ApiModelProperty(value="拒绝原因", name="rejectReason")
    private String rejectReason;

    @ApiModelProperty(value="物流公司名称", name="expressName")
    private String expressName;

    @ApiModelProperty(value="物流单号", name="expressNo")
    private String expressNo;

}
