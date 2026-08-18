package com.fuint.common.dto.decorate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 底部导航配置 DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class TabbarDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("是否启用自定义底部导航")
    private Boolean enabled;

    @ApiModelProperty("导航类型：iconText图文、image图片、text文字")
    private String type;

    @ApiModelProperty("样式配置")
    private Map<String, Object> style;

    @ApiModelProperty("导航项列表")
    private List<Map<String, Object>> items;
}
