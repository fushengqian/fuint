package com.fuint.module.merchantApi.request;

import com.fuint.common.param.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookListRequest extends PageParam implements Serializable {

    @ApiModelProperty(value="商户ID", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    @ApiModelProperty(value="状态", name="status")
    private String status;

}
