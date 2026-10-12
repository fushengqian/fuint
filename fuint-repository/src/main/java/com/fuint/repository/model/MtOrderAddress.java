package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单收货地址记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_order_address")
@Schema(name="MtOrderAddress对象", description="订单收货地址记录表")
public class MtOrderAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="地址ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="收货人姓名")
    private String name;

    @Schema(description="联系电话")
    private String mobile;

    @Schema(description="省份ID")
    private Integer provinceId;

    @Schema(description="城市ID")
    private Integer cityId;

    @Schema(description="区/县ID")
    private Integer regionId;

    @Schema(description="详细地址")
    private String detail;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="创建时间")
    private Date createTime;


}
