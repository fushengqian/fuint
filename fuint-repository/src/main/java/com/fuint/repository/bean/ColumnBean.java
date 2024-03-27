package com.fuint.repository.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * 表结构字段实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ColumnBean implements Serializable {

    @ApiModelProperty("字段名称")
    private String field;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("是否为空")
    private String isNull;

    @ApiModelProperty("备注信息")
    private String comment;

}
