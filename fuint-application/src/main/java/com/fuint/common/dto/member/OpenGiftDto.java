package com.fuint.common.dto.member;

import com.fuint.repository.model.MtCoupon;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUserGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 开卡赠礼实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class OpenGiftDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="店铺信息")
    private MtStore storeInfo;

    @Schema(description="会员等级信息")
    private MtUserGrade gradeInfo;

    @Schema(description="赠送积分")
    private Integer point;

    @Schema(description="卡券信息")
    private MtCoupon couponInfo;

    @Schema(description="卡券数量")
    private Integer couponNum;

    @Schema(description="创建时间")
    private String createTime;

    @Schema(description="更新时间")
    private String updateTime;

    @Schema(description="状态")
    private String status;

    @Schema(description="最后操作人")
    private String operator;

}

