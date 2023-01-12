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
 * 后台操作日志表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_action_log")
@ApiModel(value = "TActionLog对象", description = "后台操作日志表")
public class TActionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("操作时间")
    private Date actionTime;

    @ApiModelProperty("耗时")
    private BigDecimal timeConsuming;

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("操作模块")
    private String module;

    @ApiModelProperty("请求URL")
    private String url;

    @ApiModelProperty("操作用户账户")
    private String acctName;

    @ApiModelProperty("用户系统以及浏览器信息")
    private String userAgent;

    private Integer clientPort;
}
