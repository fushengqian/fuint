package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 积分分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PointPage extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("会员号")
    private String userNo;
}
