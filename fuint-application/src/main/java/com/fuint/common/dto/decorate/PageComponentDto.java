package com.fuint.common.dto.decorate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 页面装修组件 DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PageComponentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="组件明细ID")
    private Integer id;

    @Schema(description="组件类型")
    private String type;

    @Schema(description="组件名称")
    private String name;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="样式JSON对象")
    private Map<String, Object> style;

    @Schema(description="参数JSON对象")
    private Map<String, Object> params;

    @Schema(description="数据JSON对象")
    private Map<String, Object> data;
}
