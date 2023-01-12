package com.fuint.repository.bean;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 卡券数量对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@ApiModel(value = "卡券数量对象", description = "卡券数量对象")
public class CouponNumBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("数量")
    private Long num;
}
