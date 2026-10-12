package com.fuint.common.dto.goods;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品详情实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsDetailDto implements Serializable {

    @Schema(description="自增ID")
    private Integer goodsId;

    @Schema(description="商品名称")
    private String name;

    @Schema(description="分类ID")
    private Integer cateId;

    @Schema(description="商品条码")
    private String goodsNo;

    @Schema(description="可否单规格")
    private String isSingleSpec;

    @Schema(description="主图地址")
    private String logo;

    @Schema(description="图片地址")
    private List<String> images;

    @Schema(description="商品价格")
    private BigDecimal price;

    @Schema(description="划线价格")
    private BigDecimal linePrice;

    @Schema(description="库存")
    private Double stock;

    @Schema(description="商品重量")
    private BigDecimal weight;

    @Schema(description="初始销量")
    private Double initSale;

    @Schema(description="商品卖点")
    private String salePoint;

    @Schema(description="可否使用积分抵扣")
    private String canUsePoint;

    @Schema(description="是否积分兑换商品：Y是 N否")
    private String isPointGoods;

    @Schema(description="兑换所需积分数量")
    private Integer pointPrice;

    @Schema(description="每人限兑数量，0表示不限")
    private Integer exchangeLimit;

    @Schema(description="会员是否有折扣")
    private String isMemberDiscount;

    @Schema(description="会员等级限制，逗号分隔的等级ID，空表示不限")
    private String gradeIds;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="商品描述")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A：正常；D：删除")
    private String status;

    @Schema(description="sku列表")
    private List<GoodsSkuDto> skuList;

    @Schema(description="规格列表")
    private List<GoodsSpecDto> specList;

}

