package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 店铺商品实体
 *
 * @Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_store_goods")
@ApiModel(value = "store_goods表对象", description = "store_goods表对象")
public class MtStoreGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("所属商户")
    private Integer merchantId;

    @ApiModelProperty("所属店铺")
    private Integer storeId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，A：有效/启用；D：无效")
    private String status;

    @ApiModelProperty("最后操作人")
    private String operator;

}
