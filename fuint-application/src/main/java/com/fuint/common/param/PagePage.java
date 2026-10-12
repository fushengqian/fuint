package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 装修页面分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PagePage extends PageParam implements Serializable {

    @Schema(description="页面名称")
    private String pageName;

    @Schema(description="页面类型：index首页、custom自定义页面")
    private String pageType;

    @Schema(description="状态，A启用；N禁用")
    private String status;

}
