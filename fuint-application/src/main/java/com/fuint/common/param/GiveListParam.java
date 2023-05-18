package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 转增记录列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GiveListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="转增对象手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="类型，give = 转增，gived = 被转增", name="type")
    private String type;

}
