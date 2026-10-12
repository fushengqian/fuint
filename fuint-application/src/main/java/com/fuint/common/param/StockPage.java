package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 库存分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StockPage extends PageParam implements Serializable {

    @Schema(description="状态")
    private String status;

    @Schema(description="类型")
    private String type;

    @Schema(description="描述")
    private String description;
}
