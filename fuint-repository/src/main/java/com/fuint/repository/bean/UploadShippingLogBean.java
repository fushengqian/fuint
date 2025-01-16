package com.fuint.repository.bean;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信小程序上传发货信息对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@ApiModel(value = "微信小程序上传发货信息对象", description = "微信小程序上传发货信息对象")
public class UploadShippingLogBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    private Integer id;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderSn;

    /**
     * 状态
     */
    @ApiModelProperty("上传状态，A成功；B失败")
    private String status;

}
