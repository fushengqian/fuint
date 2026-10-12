package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 微信小程序上传发货信息实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_upload_shipping_log")
@Schema(name="upload_shipping_log表对象", description="微信小程序上传发货信息对象")
public class MtUploadShippingLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="订单手机号")
    private String mobile;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A成功；B失败")
    private String status;

}
