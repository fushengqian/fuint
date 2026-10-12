package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统消息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_message")
@Schema(name="MtMessage对象", description="系统消息")
public class MtMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="消息类型")
    private String type;

    @Schema(description="消息标题")
    private String title;

    @Schema(description="消息内容")
    private String content;

    @Schema(description="是否已读")
    private String isRead;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="参数信息")
    private String params;

    @Schema(description="是否已发送")
    private String isSend;

    @Schema(description="发送时间")
    private Date sendTime;

    @Schema(description="状态")
    private String status;

}
