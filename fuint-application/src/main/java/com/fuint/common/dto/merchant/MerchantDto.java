package com.fuint.common.dto.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 商户实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="类型")
    private String type;

    @Schema(description="微信小程序appId")
    private String wxAppId;

    @Schema(description="微信小程序秘钥")
    private String wxAppSecret;

    @Schema(description="微信公众号appId")
    private String wxOfficialAppId;

    @Schema(description="微信公众号秘钥")
    private String wxOfficialAppSecret;

    @Schema(description="商户号")
    private String no;

    @Schema(description="商户名称")
    private String name;

    @Schema(description="商户logo")
    private String logo;

    @Schema(description="联系人姓名")
    private String contact;

    @Schema(description="联系电话")
    private String phone;

    @Schema(description="联系地址")
    private String address;

    @Schema(description="备注信息")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，A：有效/启用；D：无效")
    private String status;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="有效期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @Schema(description="有效期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

}
