package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="会员ID", name="userId", requiredMode=Schema.RequiredMode.REQUIRED)
    private Integer userId;

    @Schema(description="标签ID列表", name="tagIds")
    private List<Integer> tagIds;

}
