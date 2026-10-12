package com.fuint.common.dto.goods;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtGoodsSpec;
import com.fuint.repository.model.MtStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="分配店铺ID集合")
    private String storeIds;

    @Schema(description="所属店铺信息")
    private MtStore storeInfo;

    @Schema(description="商品名称")
    private String name;

    @Schema(description="商品类型")
    private String type;

    @Schema(description="分类ID")
    private Integer cateId;

    @Schema(description="预约项目ID")
    private Integer bookId;

    @Schema(description="分类信息")
    private MtGoodsCate cateInfo;

    @Schema(description="商品条码")
    private String goodsNo;

    @Schema(description="可用平台，0：不限，1：仅会员端（小程序和h5）；2：仅收银端")
    private Integer platform;

    @Schema(description="可否单规格")
    private String isSingleSpec;

    @Schema(description="主图地址")
    private String logo;

    @Schema(description="图片地址")
    private String images;

    @Schema(description="价格")
    private BigDecimal price;

    @Schema(description="划线价格")
    private BigDecimal linePrice;

    @Schema(description="成本价格")
    private BigDecimal costPrice;

    @Schema(description="库存")
    private Double stock;

    @Schema(description="数量")
    private Double num;

    @Schema(description="服务时长")
    private Integer serviceTime;

    @Schema(description="卡券ID")
    private String couponIds;

    @Schema(description="重量")
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

    @Schema(description="skuId")
    private Integer skuId;

    @Schema(description="sku列表")
    private List<MtGoodsSku> skuList;

    @Schema(description="规格列表")
    private List<MtGoodsSpec> specList;

}

