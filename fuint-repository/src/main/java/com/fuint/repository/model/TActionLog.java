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
 * 后台操作日志表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("t_action_log")
@Schema(name="TActionLog对象", description="后台操作日志表")
public class TActionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="操作时间")
    private Date actionTime;

    @Schema(description="耗时")
    private BigDecimal timeConsuming;

    @Schema(description="客户端IP")
    private String clientIp;

    @Schema(description="操作模块")
    private String module;

    @Schema(description="请求URL")
    private String url;

    @Schema(description="操作用户账户")
    private String acctName;

    @Schema(description="用户系统以及浏览器信息")
    private String userAgent;

    @Schema(description="端口号")
    private Integer clientPort;

    @Schema(description="操作参数")
    private String param;
}
