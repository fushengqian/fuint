package com.fuint.module.clientApi.request;

import com.fuint.common.param.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 售后列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RefundListRequest extends PageParam implements Serializable {

    @ApiModelProperty(value="会员ID", name="userId")
    private Integer userId;

    @ApiModelProperty(value="状态", name="status")
    private String status;

}
