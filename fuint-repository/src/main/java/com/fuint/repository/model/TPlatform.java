package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 平台实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("t_platform")
@Schema(name="TPlatform对象", description="平台")
public class TPlatform implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="主键")
    @TableId(value = "owner_id", type = IdType.AUTO)
    private Integer ownerId;

    @Schema(description="平台名称")
    private String name;

    @Schema(description="状态 0 无效 1 有效")
    private Integer status;

    @Schema(description="描述")
    private String description;

    @Schema(description="平台类型")
    private Integer platformType;

}
