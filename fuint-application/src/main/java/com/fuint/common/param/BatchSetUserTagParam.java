package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量设置会员标签请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BatchSetUserTagParam implements Serializable {

    @ApiModelProperty(value="会员ID列表", name="userIds", required = true)
    private List<Integer> userIds;

    @ApiModelProperty(value="标签ID列表", name="tagIds")
    private List<Integer> tagIds;

}
