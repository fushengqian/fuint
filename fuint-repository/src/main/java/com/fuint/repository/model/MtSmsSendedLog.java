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
 * 短信发送记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_sms_sended_log")
@ApiModel(value = "MtSmsSendedLog对象", description = "短信发送记录表")
public class MtSmsSendedLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志ID")
    @TableId(value = "LOG_ID", type = IdType.AUTO)
    private Integer logId;

    @ApiModelProperty("手机号")
    private String mobilePhone;

    @ApiModelProperty("短信内容")
    private String content;

    @ApiModelProperty("发送时间")
    private Date sendTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;


}
