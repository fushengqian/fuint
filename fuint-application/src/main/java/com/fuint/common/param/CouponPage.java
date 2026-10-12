package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 卡券分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponPage extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="卡券名称")
    private String name;

    @Schema(description="状态")
    private String status;

    @Schema(description="分组ID")
    private Integer groupId;

    @Schema(description="卡券类型")
    private String type;

}
