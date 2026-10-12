package com.fuint.module.backendApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 提交商户请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantSubmitRequest implements Serializable {

    @Schema(description="商户ID", name="id")
    private Integer id;

    @Schema(description="商户名称", name="name")
    private String name;

    @Schema(description="商户号", name="no")
    private String no;

    @Schema(description="联系人", name="contact")
    private String contact;

    @Schema(description="手机号", name="phone")
    private String phone;

    @Schema(description="备注信息", name="description")
    private String description;

    @Schema(description="地址", name="address")
    private String address;

    @Schema(description="状态", name="status")
    private String status;

    @Schema(description="图片", name="logo")
    private String logo;

    @Schema(description="类型", name="type")
    private String type;

    @Schema(description="小程序appId", name="wxAppId")
    private String wxAppId;

    @Schema(description="小程序appSecret", name="wxAppSecret")
    private String wxAppSecret;

    @Schema(description="公众号appId", name="wxOfficialAppId")
    private String wxOfficialAppId;

    @Schema(description="公众号AppSecret", name="wxOfficialAppSecret")
    private String wxOfficialAppSecret;

    @Schema(description="结算比例", name="settleRate")
    private BigDecimal settleRate;

    @Schema(description="有效期开始时间", name="startTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @Schema(description="有效期结束时间", name="endTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

}
