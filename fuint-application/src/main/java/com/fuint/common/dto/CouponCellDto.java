package com.fuint.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 卡券导入单元实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class CouponCellDto {

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("分组ID")
    private List<Integer> groupId;

    @ApiModelProperty("发放数量")
    private List<Integer> num;

}
