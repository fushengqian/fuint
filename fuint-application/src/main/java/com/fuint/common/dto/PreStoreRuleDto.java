package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 预存规则实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class PreStoreRuleDto implements Serializable {

    @ApiModelProperty("预存金额")
    private String preStoreAmount;

    @ApiModelProperty("目标金额")
    private String targetAmount;

}
