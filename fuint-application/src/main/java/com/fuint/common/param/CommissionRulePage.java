package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 分销提成规则分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionRulePage extends PageParam implements Serializable {

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("规则名称")
    private String name;

    @ApiModelProperty("分佣对象")
    private String target;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
