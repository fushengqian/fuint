package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Schema(description="数据ID", name="id", requiredMode=Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID 不能为空")
    private Integer id;

    @Schema(description="修改状态", name="status", requiredMode=Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "状态不能为空")
    private String status;

}
