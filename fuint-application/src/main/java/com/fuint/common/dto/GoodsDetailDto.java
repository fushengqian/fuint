package com.fuint.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class GoodsDetailDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer goodsId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("分类ID")
    private Integer cateId;

    @ApiModelProperty("商品条码")
    private String goodsNo;

    @ApiModelProperty("可否单规格")
    private String isSingleSpec;

    @ApiModelProperty("主图地址")
    private String logo;

    @ApiModelProperty("图片地址")
    private List<String> images;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("划线价格")
    private BigDecimal linePrice;

    @ApiModelProperty("库存")
    private Double stock;

    @ApiModelProperty("商品重量")
    private BigDecimal weight;

    @ApiModelProperty("初始销量")
    private Double initSale;

    @ApiModelProperty("商品卖点")
    private String salePoint;

    @ApiModelProperty("可否使用积分抵扣")
    private String canUsePoint;

    @ApiModelProperty("会员是否有折扣")
    private String isMemberDiscount;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("商品描述")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态，A：正常；D：删除")
    private String status;

    @ApiModelProperty("sku列表")
    private List<GoodsSkuDto> skuList;

    @ApiModelProperty("规格列表")
    private List<GoodsSpecDto> specList;

}

