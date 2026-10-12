package com.fuint.common.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fuint.repository.model.MtUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 余额变动实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BalanceDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="会员信息")
    private MtUser userInfo;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="余额变化数量")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="备注说明")
    private String description;

    @Schema(description="状态，A正常；D删除")
    private String status;

    @Schema(description="最后操作人")
    private String operator;

}

