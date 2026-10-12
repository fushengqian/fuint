package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 积分分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PointPage extends PageParam implements Serializable {

    @Schema(description="描述")
    private String description;

    @Schema(description="状态")
    private String status;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="会员号")
    private String userNo;
}
