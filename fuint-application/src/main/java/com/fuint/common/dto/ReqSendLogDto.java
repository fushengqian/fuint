package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发放卡券记录请求DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ReqSendLogDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("类型，1：单用户发券；2：批量发券")
    private Integer type;

    @ApiModelProperty("会员ID")
    private Integer userId;

    @ApiModelProperty("导入文件名")
    private String fileName;

    @ApiModelProperty("导入文件路径")
    private String filePath;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("分组ID")
    private Integer groupId;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("发放数量")
    private Integer sendNum;

    @ApiModelProperty("发放时间")
    private Date createTime;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("uuid")
    private String uuid;

}
