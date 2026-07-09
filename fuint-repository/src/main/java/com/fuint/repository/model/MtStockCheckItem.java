package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 库存盘点明细表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_stock_check_item")
@ApiModel(value = "MtStockCheckItem对象", description = "库存盘点明细表")
public class MtStockCheckItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("盘点主表ID")
    private Integer checkId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("skuID")
    private Integer skuId;

    @ApiModelProperty("系统库存")
    private Double systemStock;

    @ApiModelProperty("实际库存")
    private Double actualStock;

    @ApiModelProperty("差异数量")
    private Double diffStock;

    @ApiModelProperty("备注说明")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，A正常；D删除")
    private String status;

}
