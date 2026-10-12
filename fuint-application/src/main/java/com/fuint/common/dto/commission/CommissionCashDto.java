package com.fuint.common.dto.commission;

import com.fuint.common.dto.order.OrderUserDto;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销提成提现实体
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionCashDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="结算单号")
    private String settleNo;

    @Schema(description="结算uuid")
    private String uuid;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="所属店铺信息")
    private MtStore storeInfo;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="用户信息")
    private OrderUserDto userInfo;

    @Schema(description="员工ID")
    private Integer staffId;

    @Schema(description="所属店铺信息")
    private MtStaff staffInfo;

    @Schema(description="金额")
    private BigDecimal amount;

    @Schema(description="备注")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

}
