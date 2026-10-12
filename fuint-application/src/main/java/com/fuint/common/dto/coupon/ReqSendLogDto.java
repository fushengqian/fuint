package com.fuint.common.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 发放卡券记录请求DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class ReqSendLogDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="类型，1：单用户发券；2：批量发券")
    private Integer type;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="导入文件名")
    private String fileName;

    @Schema(description="导入文件路径")
    private String filePath;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="分组ID")
    private Integer groupId;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="分组名称")
    private String groupName;

    @Schema(description="发放数量")
    private Integer sendNum;

    @Schema(description="发放时间")
    private Date createTime;

    @Schema(description="操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

    @Schema(description="uuid")
    private String uuid;

}
