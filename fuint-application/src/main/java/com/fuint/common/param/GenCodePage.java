package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
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

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("表名")
    private String tableName;

    @ApiModelProperty("状态")
    private String status;
}
