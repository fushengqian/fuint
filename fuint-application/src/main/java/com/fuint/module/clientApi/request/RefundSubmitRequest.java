package com.fuint.module.clientApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 售后列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RefundSubmitRequest implements Serializable {

    @ApiModelProperty(value="会员ID", name="userId")
    private Integer userId;

    @ApiModelProperty(value="订单ID", name="orderId")
    private Integer orderId;

    @ApiModelProperty(value="备注信息", name="remark")
    private String remark;

    @ApiModelProperty(value="售后类型", name="type")
    private String type;

    @ApiModelProperty(value="售后图片", name="images")
    private List<String> images;

}
