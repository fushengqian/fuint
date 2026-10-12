package com.fuint.module.clientApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="会员ID", name="userId")
    private Integer userId;

    @Schema(description="订单ID", name="orderId")
    private Integer orderId;

    @Schema(description="备注信息", name="remark")
    private String remark;

    @Schema(description="售后类型", name="type")
    private String type;

    @Schema(description="售后图片", name="images")
    private List<String> images;

}
