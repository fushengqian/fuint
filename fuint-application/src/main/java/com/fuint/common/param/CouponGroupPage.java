package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 卡券分组分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponGroupPage extends PageParam implements Serializable {

    @Schema(description="分组名称")
    private String name;

    @Schema(description="状态")
    private String status;

    @Schema(description="分组ID")
    private Integer id;

}
