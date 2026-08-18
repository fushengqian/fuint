package com.fuint.common.dto.decorate;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("组件明细ID")
    private Integer id;

    @ApiModelProperty("组件类型")
    private String type;

    @ApiModelProperty("组件名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("样式JSON对象")
    private Map<String, Object> style;

    @ApiModelProperty("参数JSON对象")
    private Map<String, Object> params;

    @ApiModelProperty("数据JSON对象")
    private Map<String, Object> data;
}
