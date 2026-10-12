package com.fuint.common.dto.decorate;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="主题ID")
    private String themeId;

    @Schema(description="主题名称")
    private String themeName;

    @Schema(description="颜色配置")
    private Map<String, String> colors;
}
