package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 短信模板分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SmsTemplatePage extends PageParam implements Serializable {

    @Schema(description="模板名称")
    private String name;

    @Schema(description="模板标识")
    private String uname;

    @Schema(description="模板编码")
    private String code;

    @Schema(description="状态")
    private String status;
}
