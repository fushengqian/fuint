package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设置会员标签请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SetUserTagParam implements Serializable {

    @ApiModelProperty(value="会员ID", name="userId", required = true)
    private Integer userId;

    @ApiModelProperty(value="标签ID列表", name="tagIds")
    private List<Integer> tagIds;

}
