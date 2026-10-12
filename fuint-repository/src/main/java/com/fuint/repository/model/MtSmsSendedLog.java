package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短信发送记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_sms_sended_log")
@Schema(name="MtSmsSendedLog对象", description="短信发送记录表")
public class MtSmsSendedLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="日志ID")
    @TableId(value = "LOG_ID", type = IdType.AUTO)
    private Integer logId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="手机号")
    private String mobilePhone;

    @Schema(description="短信内容")
    private String content;

    @Schema(description="发送时间")
    private Date sendTime;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;


}
