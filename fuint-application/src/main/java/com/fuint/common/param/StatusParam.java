package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 状态修改请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StatusParam implements Serializable {

    @ApiModelProperty(value="数据ID", name="id")
    private Integer id;

    @ApiModelProperty(value="修改状态", name="status")
    private String status;

}
