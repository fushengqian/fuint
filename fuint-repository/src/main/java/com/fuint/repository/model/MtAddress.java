package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 会员地址表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_address")
@ApiModel(value = "MtAddress对象", description = "会员地址表")
public class MtAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("收货人姓名")
    private String name;

    @ApiModelProperty("收货手机号")
    private String mobile;

    @ApiModelProperty("省份ID")
    private Integer provinceId;

    @ApiModelProperty("城市ID")
    private Integer cityId;

    @ApiModelProperty("区/县ID")
    private Integer regionId;

    @ApiModelProperty("详细地址")
    private String detail;

    @ApiModelProperty("是否默认")
    private String isDefault;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态")
    private String status;


}
