package com.fuint.common.dto.decorate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 页面装修 DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PageDecorationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="页面ID")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="页面名称")
    private String pageName;

    @Schema(description="页面类型：index首页、custom自定义页面")
    private String pageType;

    @Schema(description="是否默认：Y是 N否")
    private String isDefault;

    @Schema(description="分享标题")
    private String shareTitle;

    @Schema(description="分享logo")
    private String shareLogo;

    @Schema(description="状态：A启用 N禁用 D删除")
    private String status;

    @Schema(description="组件列表")
    private List<PageComponentDto> components;
}
