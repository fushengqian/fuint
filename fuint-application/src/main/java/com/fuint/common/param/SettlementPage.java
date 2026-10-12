package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 结算分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SettlementPage extends PageParam implements Serializable {

    @Schema(description="状态")
    private String status;

    @Schema(description="描述")
    private String description;
}
