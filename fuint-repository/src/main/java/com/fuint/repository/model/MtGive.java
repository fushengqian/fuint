package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 转赠记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_give")
@Schema(name="MtGive对象", description="转赠记录表")
public class MtGive implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="获赠者用户ID")
    private Integer userId;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="赠送者用户ID")
    private Integer giveUserId;

    @Schema(description="赠予对象手机号")
    private String mobile;

    @Schema(description="用户手机")
    private String userMobile;

    @Schema(description="券组ID，逗号隔开")
    private String groupIds;

    @Schema(description="券组名称，逗号隔开")
    private String groupNames;

    @Schema(description="券ID，逗号隔开")
    private String couponIds;

    @Schema(description="券名称，逗号隔开")
    private String couponNames;

    @Schema(description="数量")
    private Integer num;

    @Schema(description="总金额")
    private BigDecimal money;

    @Schema(description="备注")
    private String note;

    @Schema(description="留言")
    private String message;

    @Schema(description="赠送时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，A正常；C取消")
    private String status;


}
