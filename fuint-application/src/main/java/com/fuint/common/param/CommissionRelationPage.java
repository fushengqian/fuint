package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 分销提成邀请记录分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRelationPage extends PageParam implements Serializable {

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("商户号")
    private String merchantNo;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("姓名")
    private String realName;

    @ApiModelProperty("类型")
    private String subUserId;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
