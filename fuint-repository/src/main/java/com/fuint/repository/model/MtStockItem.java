package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库存管理明细表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_stock_item")
@Schema(name="MtStockItem对象", description="库存管理明细表")
public class MtStockItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="库存管理ID")
    private Integer stockId;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="SKU")
    private Integer skuId;

    @Schema(description="数量")
    private Double num;

    @Schema(description="备注说明")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，A正常；D删除")
    private String status;

}
