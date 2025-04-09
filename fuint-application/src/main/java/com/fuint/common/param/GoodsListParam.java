package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="商品名称", name="name")
    private String name;

    @ApiModelProperty(value="商品编码", name="goodsNo")
    private String goodsNo;

    @ApiModelProperty(value="是否单规格", name="isSingleSpec")
    private String isSingleSpec;

    @ApiModelProperty(value="商品类型", name="type")
    private String type;

    @ApiModelProperty(value="商品状态", name="status")
    private String status;

    @ApiModelProperty(value="所属商户", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="所属店铺", name="storeId")
    private Integer storeId;

    @ApiModelProperty(value="是否有库存", name="stock")
    private String stock;

    @ApiModelProperty(value="商品分类", name="cateId")
    private Integer cateId;

}
