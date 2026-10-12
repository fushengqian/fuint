package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券核销流水请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ConfirmLogPage extends PageParam implements Serializable {

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="状态")
    private String status;

    @Schema(description="手机号")
    private String mobile;

}
