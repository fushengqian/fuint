package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("t_duty")
@Schema(name="TDuty对象", description="角色表")
public class TDuty implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="角色ID")
    @TableId(value = "duty_id", type = IdType.AUTO)
    private Integer dutyId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="角色名称")
    private String dutyName;

    @Schema(description="状态(A: 可用  D: 禁用)")
    private String status;

    @Schema(description="描述")
    private String description;

    @Schema(description="角色类型")
    private String dutyType;
}
