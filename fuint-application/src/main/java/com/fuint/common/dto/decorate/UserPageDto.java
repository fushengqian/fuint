package com.fuint.common.dto.decorate;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="页面名称")
    private String pageName;

    @Schema(description="分享标题")
    private String shareTitle;

    @Schema(description="分享logo")
    private String shareLogo;

    @Schema(description="组件列表")
    private List<PageComponentDto> components;
}
