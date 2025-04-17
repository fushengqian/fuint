package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单列表请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class StaffParam extends PageParam implements Serializable {

    @ApiModelProperty(value="ID", name="id")
    private Integer id;

    @ApiModelProperty(value="商户ID", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    @ApiModelProperty(value="类别", name="category")
    private Integer category;

    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="真实姓名", name="realName")
    private String realName;

    @ApiModelProperty(value="备注信息", name="description")
    private String description;

    @ApiModelProperty(value="审核状态", name="auditedStatus")
    private String auditedStatus;

}
