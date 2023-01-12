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
 * 规格表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_goods_spec")
@ApiModel(value = "MtGoodsSpec对象", description = "规格表")
public class MtGoodsSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("规格名称")
    private String name;

    @ApiModelProperty("规格值")
    private String value;

    @ApiModelProperty("状态")
    private String status;


}
