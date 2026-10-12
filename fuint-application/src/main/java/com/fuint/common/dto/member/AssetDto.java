package com.fuint.common.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 个人资产实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AssetDto {

    @Schema(description="次卡数量")
    private Integer timer;

    @Schema(description="储值卡数量")
    private Integer prestore;

    @Schema(description="优惠券数量")
    private Integer coupon;
}
