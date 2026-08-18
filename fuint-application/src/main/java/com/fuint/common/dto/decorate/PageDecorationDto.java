package com.fuint.common.dto.decorate;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("页面ID")
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("页面名称")
    private String pageName;

    @ApiModelProperty("页面类型：index首页、custom自定义页面")
    private String pageType;

    @ApiModelProperty("是否默认：Y是 N否")
    private String isDefault;

    @ApiModelProperty("分享标题")
    private String shareTitle;

    @ApiModelProperty("分享logo")
    private String shareLogo;

    @ApiModelProperty("状态：A启用 N禁用 D删除")
    private String status;

    @ApiModelProperty("组件列表")
    private List<PageComponentDto> components;
}
