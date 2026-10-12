package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 收获地址详情请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AddressDetailParam implements Serializable {

    @Schema(description="收获地址ID", name="addressId")
    private Integer addressId;

}
