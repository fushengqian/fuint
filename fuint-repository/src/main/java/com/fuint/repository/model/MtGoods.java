package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_goods")
@ApiModel(value = "MtGoods对象", description = "MtGoods对象")
public class MtGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品类型")
    private String type;

    @ApiModelProperty("分类ID")
    private Integer cateId;

    @ApiModelProperty("商品编码")
    private String goodsNo;

    @ApiModelProperty("是否单规格")
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

    @ApiModelProperty("关联卡券")
    private String couponIds;

    @ApiModelProperty("服务时长")
    private Integer serviceTime;

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

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("A：正常；D：删除")
    private String status;


}
