package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 代码生成分页查询参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GenCodePage extends PageParam implements Serializable {

    @Schema(description="表名")
    private String tableName;

    @Schema(description="状态")
    private String status;
}
