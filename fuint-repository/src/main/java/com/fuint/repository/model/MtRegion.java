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
 * 省市区数据表
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_region")
@ApiModel(value = "MtRegion对象", description = "省市区数据表")
public class MtRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("区划信息ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("区划名称")
    private String name;

    @ApiModelProperty("父级ID")
    private Integer pid;

    @ApiModelProperty("区划编码")
    private String code;

    @ApiModelProperty("层级(1省级 2市级 3区/县级)")
    private Integer level;


}
