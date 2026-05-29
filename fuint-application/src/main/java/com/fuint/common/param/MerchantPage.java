package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商户分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantPage extends PageParam implements Serializable {

    @ApiModelProperty("商户ID")
    private String id;

    @ApiModelProperty("商户名称")
    private String name;

    @ApiModelProperty("状态")
    private String status;

}
