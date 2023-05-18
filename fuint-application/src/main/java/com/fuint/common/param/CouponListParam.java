package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 卡券列表请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponListParam extends PageParam implements Serializable {

    @ApiModelProperty(value="卡券类型", name="type")
    private String type;

    @ApiModelProperty(value="领取所需积分", name="needPoint")
    private Integer needPoint;

    @ApiModelProperty(value="发放方式", name="sendWay")
    private String sendWay;

    @ApiModelProperty(value="排序类型", name="sortType")
    private String sortType;

    @ApiModelProperty(value="下单会员ID", name="userId")
    private Integer userId;

    @ApiModelProperty(value="状态", name="status")
    private String status;

}
