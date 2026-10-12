package com.fuint.common.dto.member;

import com.fuint.repository.model.MtUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分记录实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class PointDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="会员信息")
    private MtUser userInfo;

    @Schema(description="订单号")
    private String orderSn;

    @Schema(description="积分变化数量")
    private Integer amount;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="备注说明")
    private String description;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A正常；D删除")
    private String status;

}

