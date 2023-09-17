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
 * 文章
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_article")
@ApiModel(value = "MtArticle对象", description = "MtArticle对象")
public class MtArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("简介")
    private String brief;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("链接地址")
    private String url;

    @ApiModelProperty("图片地址")
    private String image;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("点击数")
    private Long click;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("A：正常；N：禁用；D：删除")
    private String status;

}
