package com.fuint.common.dto.book;

import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预约分类实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookCateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="分类名称")
    private String name;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="图片地址")
    private String logo;

    @Schema(description="描述")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="A：正常；D：删除")
    private String status;
}
