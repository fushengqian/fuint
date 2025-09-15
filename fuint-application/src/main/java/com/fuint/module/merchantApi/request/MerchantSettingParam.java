package com.fuint.module.merchantApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 员工列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantSettingParam implements Serializable {

    @ApiModelProperty(value="商户ID", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    @ApiModelProperty("商户名称")
    private String name;

    @ApiModelProperty("商户logo")
    private String logo;

    @ApiModelProperty("联系人姓名")
    private String contact;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("营业状态")
    private String status;

}
