package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约订单分页请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookItemPage extends PageParam implements Serializable {

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("分类ID")
    private Integer cateId;

    @ApiModelProperty("联系人")
    private String contact;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("状态，A正常；D作废")
    private String status;

}
