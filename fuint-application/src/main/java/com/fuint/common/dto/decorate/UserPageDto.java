package com.fuint.common.dto.decorate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 个人中心配置 DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserPageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("页面名称")
    private String pageName;

    @ApiModelProperty("分享标题")
    private String shareTitle;

    @ApiModelProperty("分享logo")
    private String shareLogo;

    @ApiModelProperty("组件列表")
    private List<PageComponentDto> components;
}
