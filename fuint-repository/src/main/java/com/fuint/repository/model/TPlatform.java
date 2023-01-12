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
 * <p>
 * 
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_platform")
@ApiModel(value = "TPlatform对象", description = "")
public class TPlatform implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "owner_id", type = IdType.AUTO)
    private Integer ownerId;

    @ApiModelProperty("平台名称")
    private String name;

    @ApiModelProperty("状态 0 无效 1 有效")
    private Integer status;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("平台类型 1：免税易购 2：其他体验店")
    private Integer platformType;


}
