package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 状态修改请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="数据ID", name="id", required = true)
    @NotNull(message = "ID 不能为空")
    private Integer id;

    @ApiModelProperty(value="修改状态", name="status", required = true)
    @NotBlank(message = "状态不能为空")
    private String status;

}
