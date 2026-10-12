package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 开卡赠礼分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OpenGiftPage extends PageParam implements Serializable {

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="会员等级")
    private Integer gradeId;

    @Schema(description="状态")
    private String status;
}
