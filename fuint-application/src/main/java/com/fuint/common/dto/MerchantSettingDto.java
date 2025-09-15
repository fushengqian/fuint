package com.fuint.common.dto;

import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商户设置实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantSettingDto implements Serializable {

    @ApiModelProperty("自增ID")
    private Integer id;

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
