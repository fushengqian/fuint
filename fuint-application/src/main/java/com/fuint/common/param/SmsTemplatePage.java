package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("模板名称")
    private String name;

    @ApiModelProperty("模板标识")
    private String uname;

    @ApiModelProperty("模板编码")
    private String code;

    @ApiModelProperty("状态")
    private String status;
}
