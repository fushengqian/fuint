package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 卡券分组数据DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GroupDataDto implements Serializable {

    @ApiModelProperty("发放数量")
    private Integer sendNum;

    @ApiModelProperty("未发放数量")
    private Integer unSendNum;

    @ApiModelProperty("使用数量")
    private Integer useNum;

    @ApiModelProperty("过期数量")
    private Integer expireNum;

    @ApiModelProperty("取消数量")
    private Integer cancelNum;
}
