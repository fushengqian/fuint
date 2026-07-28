package com.fuint.common.dto.content;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("主键ID")
    private Integer id;

    @ApiModelProperty("服务名称")
    private String name;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("类型：link链接, button按钮")
    private String type;

    @ApiModelProperty("跳转链接")
    private String url;

    @ApiModelProperty("开放类型(contact等)")
    private String openType;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态 A：正常；D：删除")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

}
