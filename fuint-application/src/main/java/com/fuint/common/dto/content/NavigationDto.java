package com.fuint.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 导航栏实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class NavigationDto implements Serializable {

    @Schema(description="导航名称")
    private String name;

    @Schema(description="导航提示")
    private String tips;

    @Schema(description="URL")
    private String url;

    @Schema(description="图标")
    private String icon;

    @Schema(description="图标完整路径")
    private String iconUrl;

    @Schema(description="导航排序")
    private Integer sort;

    @Schema(description="状态")
    private String status;

}
