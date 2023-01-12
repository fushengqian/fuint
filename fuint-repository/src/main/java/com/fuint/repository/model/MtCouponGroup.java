package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 优惠券组
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_coupon_group")
@ApiModel(value = "MtCouponGroup对象", description = "优惠券组")
public class MtCouponGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("券组名称")
    private String name;

    @ApiModelProperty("价值金额")
    private BigDecimal money;

    @ApiModelProperty("券种类数量")
    private Integer num;

    @ApiModelProperty("发行数量")
    private Integer total;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("创建日期")
    private Date createTime;

    @ApiModelProperty("更新日期")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("A：正常；D：删除")
    private String status;


}
