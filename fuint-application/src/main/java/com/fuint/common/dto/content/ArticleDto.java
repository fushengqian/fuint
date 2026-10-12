package com.fuint.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ArticleDto implements Serializable {

    @Schema(description="账户主键ID")
    private Integer id;

    @Schema(description="标题")
    private String title;

    @Schema(description="简介")
    private String brief;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺")
    private Integer storeId;

    @Schema(description="链接地址")
    private String url;

    @Schema(description="点击数")
    private Long click;

    @Schema(description="图片地址")
    private String image;

    @Schema(description="描述")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态 A：正常；D：删除")
    private String status;

    @Schema(description="排序")
    private Integer sort;

}

