package com.fuint.common.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信模板实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class SmsTemplateDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="模板名称")
    private String name;

    @Schema(description="模板英文名称")
    private String uname;

    @Schema(description="模板编码")
    private String code;

    @Schema(description="模板内容")
    private String content;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，1：正常；2：删除")
    private String status;

}
