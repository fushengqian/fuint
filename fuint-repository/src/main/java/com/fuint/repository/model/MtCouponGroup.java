package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 优惠券组
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_coupon_group")
@Schema(name="MtCouponGroup对象", description="优惠券组")
public class MtCouponGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="券组名称")
    private String name;

    @Schema(description="价值金额")
    private BigDecimal money;

    @Schema(description="券种类数量")
    private Integer num;

    @Schema(description="发行数量")
    private Integer total;

    @Schema(description="备注")
    private String description;

    @Schema(description="创建日期")
    private Date createTime;

    @Schema(description="更新日期")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="A：正常；D：删除")
    private String status;


}
