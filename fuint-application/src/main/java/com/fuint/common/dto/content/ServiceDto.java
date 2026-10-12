package com.fuint.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 服务DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ServiceDto implements Serializable {

    @Schema(description="主键ID")
    private Integer id;

    @Schema(description="服务名称")
    private String name;

    @Schema(description="图标")
    private String icon;

    @Schema(description="类型：link链接, button按钮")
    private String type;

    @Schema(description="跳转链接")
    private String url;

    @Schema(description="开放类型(contact等)")
    private String openType;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="状态 A：正常；D：删除")
    private String status;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

}
