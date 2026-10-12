package com.fuint.common.dto.content;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 焦点图实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BannerDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="标题")
    private String title;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="链接地址")
    private String url;

    @Schema(description="图片地址")
    private String image;

    @Schema(description="描述信息")
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

    @Schema(description="状态，A正常；D删除")
    private String status;
}

