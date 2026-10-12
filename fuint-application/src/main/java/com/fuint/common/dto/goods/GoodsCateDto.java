package com.fuint.common.dto.goods;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商品分类DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class GoodsCateDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属商户名称")
    private String merchantName;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="所属店铺名称")
    private String storeName;

    @Schema(description="分类名称")
    private String name;

    @Schema(description="LOGO地址")
    private String logo;

    @Schema(description="分类描述")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="A：正常；D：删除")
    private String status;
}
