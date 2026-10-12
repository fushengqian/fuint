package com.fuint.common.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 卡券导入单元实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CouponCellDto {

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="分组ID")
    private List<Integer> groupId;

    @Schema(description="发放数量")
    private List<Integer> num;

}
