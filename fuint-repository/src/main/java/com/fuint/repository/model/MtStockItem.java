package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 库存管理明细表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_stock_item")
@ApiModel(value="MtStockItem对象", description="库存管理明细表")
public class MtStockItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("库存管理ID")
    private Integer stockId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("SKU")
    private Integer skuId;

    @ApiModelProperty("数量")
    private Integer num;

    @ApiModelProperty("备注说明")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，A正常；D删除")
    private String status;

}
