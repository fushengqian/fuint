package com.fuint.repository.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 表结构字段实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ColumnBean implements Serializable {

    @Schema(description="字段名称")
    private String field;

    @Schema(description="类型")
    private String type;

    @Schema(description="是否为空")
    private String isNull;

    @Schema(description="备注信息")
    private String comment;

}
