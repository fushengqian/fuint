package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员标签实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user_tag")
@ApiModel(value = "MtUserTag对象", description = "会员标签表")
public class MtUserTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("标签名称")
    private String name;

    @ApiModelProperty("标签颜色")
    private String color;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("标签描述")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态，A：激活；N：禁用；D：删除")
    private String status;
}
