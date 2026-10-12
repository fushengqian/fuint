package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺商品实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_store_goods")
@Schema(name="store_goods表对象", description="store_goods表对象")
public class MtStoreGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="所属商户")
    private Integer merchantId;

    @Schema(description="所属店铺")
    private Integer storeId;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，A：有效/启用；D：无效")
    private String status;

    @Schema(description="最后操作人")
    private String operator;

}
