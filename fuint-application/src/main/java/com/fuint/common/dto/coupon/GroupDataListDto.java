package com.fuint.common.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 卡券分组数据DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GroupDataListDto {

    @Schema(description="键值")
    private String key;

    @Schema(description="数据")
    private GroupDataDto data;

}
