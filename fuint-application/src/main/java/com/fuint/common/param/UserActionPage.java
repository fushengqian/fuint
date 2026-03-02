package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员行为分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class UserActionPage extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("状态")
    private String status;
}
