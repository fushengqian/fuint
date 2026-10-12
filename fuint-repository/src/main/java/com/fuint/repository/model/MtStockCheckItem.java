package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库存盘点明细表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_stock_check_item")
@Schema(name="MtStockCheckItem对象", description="库存盘点明细表")
public class MtStockCheckItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="盘点主表ID")
    private Integer checkId;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="skuID")
    private Integer skuId;

    @Schema(description="系统库存")
    private Double systemStock;

    @Schema(description="实际库存")
    private Double actualStock;

    @Schema(description="差异数量")
    private Double diffStock;

    @Schema(description="备注说明")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，A正常；D删除")
    private String status;

}
