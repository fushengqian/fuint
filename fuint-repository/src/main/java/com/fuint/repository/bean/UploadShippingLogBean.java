package com.fuint.repository.bean;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信小程序上传发货信息对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@Schema(name="微信小程序上传发货信息对象", description="微信小程序上传发货信息对象")
public class UploadShippingLogBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @Schema(description="订单ID")
    private Integer id;

    /**
     * 订单号
     */
    @Schema(description="订单号")
    private String orderSn;

    /**
     * 状态
     */
    @Schema(description="上传状态，A成功；B失败")
    private String status;

}
