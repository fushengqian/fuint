package com.fuint.common.dto.commission;

import com.fuint.common.dto.order.OrderUserDto;
import com.fuint.repository.model.MtCommissionRule;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分销提成记录实体
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class CommissionLogDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="分佣类型")
    private String type;

    @Schema(description="分佣对象")
    private String target;

    @Schema(description="分佣类型名称")
    private String typeName;

    @Schema(description="分佣等级")
    private Integer level;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="用户信息")
    private OrderUserDto userInfo;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="所属店铺信息")
    private MtStore storeInfo;

    @Schema(description="员工ID")
    private Integer staffId;

    @Schema(description="所属店铺信息")
    private MtStaff staffInfo;

    @Schema(description="订单ID")
    private Integer orderId;

    @Schema(description="订单信息")
    private MtOrder orderInfo;

    @Schema(description="分佣金额")
    private BigDecimal amount;

    @Schema(description="规则ID")
    private Integer ruleId;

    @Schema(description="分佣规则信息")
    private MtCommissionRule ruleInfo;

    @Schema(description="备注")
    private String description;

    @Schema(description="提现记录ID")
    private Integer cashId;

    @Schema(description="最后操作人")
    private String isCash;

    @Schema(description="提现时间")
    private Date cashTime;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态")
    private String status;

}
