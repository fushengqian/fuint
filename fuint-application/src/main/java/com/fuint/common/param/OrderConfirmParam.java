package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单核销请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderConfirmParam implements Serializable {

    @ApiModelProperty(value="核销码", name="code")
    private String code;

    @ApiModelProperty(value="订单ID", name="orderId")
    private Integer orderId;

    @ApiModelProperty(value="核销备注", name="remark")
    private String remark;

}
