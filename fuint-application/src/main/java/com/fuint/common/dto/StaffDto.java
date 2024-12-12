package com.fuint.common.dto;

import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtStore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 员工实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * */
@Data
public class StaffDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("员工类别")
    private Integer category;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("微信号")
    private String wechat;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("审核状态，A：审核通过；U：未审核；D：无效; ")
    private String auditedStatus;

    @ApiModelProperty("审核时间")
    private Date auditedTime;

    @ApiModelProperty("备注")
    private String description;

    @ApiModelProperty("商户信息")
    private MtMerchant merchantInfo;

    @ApiModelProperty("店铺信息")
    private MtStore storeInfo;

}
