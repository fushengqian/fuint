package com.fuint.common.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 卡券分组数据DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GroupDataDto implements Serializable {

    @Schema(description="发放数量")
    private Integer sendNum;

    @Schema(description="未发放数量")
    private Integer unSendNum;

    @Schema(description="使用数量")
    private Integer useNum;

    @Schema(description="过期数量")
    private Integer expireNum;

    @Schema(description="取消数量")
    private Integer cancelNum;
}
