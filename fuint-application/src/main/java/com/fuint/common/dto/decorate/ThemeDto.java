package com.fuint.common.dto.decorate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 主题配置 DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ThemeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主题ID")
    private String themeId;

    @ApiModelProperty("主题名称")
    private String themeName;

    @ApiModelProperty("颜色配置")
    private Map<String, String> colors;
}
