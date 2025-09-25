package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("导航名称")
    private String name;

    @ApiModelProperty("导航提示")
    private String tips;

    @ApiModelProperty("URL")
    private String url;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("图标完整路径")
    private String iconUrl;

    @ApiModelProperty("导航排序")
    private Integer sort;

    @ApiModelProperty("状态")
    private String status;

}
