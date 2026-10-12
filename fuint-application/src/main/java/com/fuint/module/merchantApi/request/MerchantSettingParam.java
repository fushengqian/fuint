package com.fuint.module.merchantApi.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description="商户ID", name="merchantId")
    private Integer merchantId;

    @Schema(description="店铺ID", name="storeId")
    private Integer storeId;

    @Schema(description="商户名称")
    private String name;

    @Schema(description="商户logo")
    private String logo;

    @Schema(description="联系人姓名")
    private String contact;

    @Schema(description="联系电话")
    private String phone;

    @Schema(description="营业状态")
    private String status;

}
