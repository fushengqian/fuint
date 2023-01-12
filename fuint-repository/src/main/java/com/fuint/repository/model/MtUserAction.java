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
 * 会员行为信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_user_action")
@ApiModel(value = "MtUserAction对象", description = "会员行为信息")
public class MtUserAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("行为类别")
    private String action;

    @ApiModelProperty("备注信息")
    private String description;

    @ApiModelProperty("参数")
    private String param;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态，A：激活；N：禁用；D：删除")
    private String status;

    @ApiModelProperty("最后操作人")
    private String operator;
}
