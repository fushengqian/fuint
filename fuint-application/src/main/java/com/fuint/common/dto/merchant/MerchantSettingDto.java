package com.fuint.common.dto.merchant;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户设置实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MerchantSettingDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

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
