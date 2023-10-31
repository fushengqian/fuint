package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ArticleDto implements Serializable {

    @ApiModelProperty("账户主键ID")
    private Integer id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("简介")
    private String brief;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺")
    private Integer storeId;

    @ApiModelProperty("链接地址")
    private String url;

    @ApiModelProperty("点击数")
    private Long click;

    @ApiModelProperty("图片地址")
    private String image;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态 A：正常；D：删除")
    private String status;

    @ApiModelProperty("排序")
    private Integer sort;

}

