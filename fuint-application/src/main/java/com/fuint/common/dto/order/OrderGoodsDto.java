package com.fuint.common.dto.order;

import com.fuint.common.dto.goods.GoodsSpecValueDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单商品实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OrderGoodsDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="预约项目ID")
    private Integer bookId;

    @Schema(description="我的预约ID")
    private Integer myBookId;

    @Schema(description="订单类型")
    private String type;

    @Schema(description="商品名称")
    private String name;

    @Schema(description="价格")
    private String price;

    @Schema(description="折扣")
    private String discount;

    @Schema(description="购买数量")
    private Double num;

    @Schema(description="图片")
    private String image;

    @Schema(description="skuId")
    private Integer skuId;

    @Schema(description="规格列表")
    private List<GoodsSpecValueDto> specList;

}

