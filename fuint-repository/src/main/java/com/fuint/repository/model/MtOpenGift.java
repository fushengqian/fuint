package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员开卡赠礼
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_open_gift")
@Schema(name="MtOpenGift对象", description="会员开卡赠礼")
public class MtOpenGift implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="会员等级ID")
    private Integer gradeId;

    @Schema(description="赠送积分")
    private Integer point;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="卡券数量")
    private Integer couponNum;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态")
    private String status;

    @Schema(description="最后操作人")
    private String operator;
}
