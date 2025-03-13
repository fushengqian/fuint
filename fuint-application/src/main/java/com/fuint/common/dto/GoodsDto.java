package com.fuint.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fuint.repository.model.MtGoodsCate;
import com.fuint.repository.model.MtGoodsSku;
import com.fuint.repository.model.MtGoodsSpec;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("分配店铺ID集合")
    private String storeIds;

    @ApiModelProperty("所属店铺信息")
    private MtStore storeInfo;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品类型")
    private String type;

    @ApiModelProperty("分类ID")
    private Integer cateId;

    @ApiModelProperty("分类信息")
    private MtGoodsCate cateInfo;

    @ApiModelProperty("商品条码")
    private String goodsNo;

    @ApiModelProperty("可否单规格")
    private String isSingleSpec;

    @ApiModelProperty("主图地址")
    private String logo;

    @ApiModelProperty("图片地址")
    private String images;

    @ApiModelProperty("价格")
    private BigDecimal price;

    @ApiModelProperty("划线价格")
    private BigDecimal linePrice;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("数量")
    private Integer num;

    @ApiModelProperty("服务时长")
    private Integer serviceTime;

    @ApiModelProperty("卡券ID")
    private String couponIds;

    @ApiModelProperty("重量")
    private BigDecimal weight;

    @ApiModelProperty("初始销量")
    private Integer initSale;

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

    @ApiModelProperty("skuId")
    private Integer skuId;

    @ApiModelProperty("sku列表")
    private List<MtGoodsSku> skuList;

    @ApiModelProperty("规格列表")
    private List<MtGoodsSpec> specList;

}

