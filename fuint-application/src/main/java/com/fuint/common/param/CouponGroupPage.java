package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 卡券分组分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponGroupPage extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分组名称")
    private String name;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("分组ID")
    private Integer id;

}
