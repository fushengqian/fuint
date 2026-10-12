package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 规格表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_goods_spec")
@Schema(name="MtGoodsSpec对象", description="规格表")
public class MtGoodsSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="规格名称")
    private String name;

    @Schema(description="规格值")
    private String value;

    @Schema(description="状态")
    private String status;


}
