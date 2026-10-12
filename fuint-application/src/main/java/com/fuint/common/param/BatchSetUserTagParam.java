package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="会员ID列表", name="userIds", requiredMode=Schema.RequiredMode.REQUIRED)
    private List<Integer> userIds;

    @Schema(description="标签ID列表", name="tagIds")
    private List<Integer> tagIds;

}
