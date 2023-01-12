package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_duty")
@ApiModel(value = "TDuty对象", description = "角色表")
public class TDuty implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("角色ID")
    @TableId(value = "duty_id", type = IdType.AUTO)
    private Integer dutyId;

    @ApiModelProperty("角色名称")
    private String dutyName;

    @ApiModelProperty("状态(A: 可用  D: 禁用)")
    private String status;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("角色类型")
    private String dutyType;
}
