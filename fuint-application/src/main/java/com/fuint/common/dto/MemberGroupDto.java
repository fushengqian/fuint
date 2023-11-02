package com.fuint.common.dto;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 会员分组实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class MemberGroupDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分组ID")
    private Integer id;

    @ApiModelProperty("分组名称")
    private String name;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("默认店铺")
    private Integer storeId;

    @ApiModelProperty("父ID")
    private Integer parentId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，A：激活；N：禁用；D：删除")
    private String status;

    @ApiModelProperty("备注信息")
    private String description;

    @ApiModelProperty("最后操作人")
    private String operator;
}
