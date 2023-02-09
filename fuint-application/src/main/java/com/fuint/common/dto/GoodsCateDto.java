package com.fuint.common.dto;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品分类
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsCateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属商户名称")
    private String merchantName;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("所属店铺名称")
    private String storeName;

    @ApiModelProperty("分类名称")
    private String name;

    @ApiModelProperty("LOGO地址")
    private String logo;

    @ApiModelProperty("分类描述")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("A：正常；D：删除")
    private String status;
}
