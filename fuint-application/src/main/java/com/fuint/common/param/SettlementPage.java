package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 结算分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SettlementPage extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("描述")
    private String description;
}
