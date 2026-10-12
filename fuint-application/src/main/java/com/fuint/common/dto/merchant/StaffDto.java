package com.fuint.common.dto.merchant;

import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 员工实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * */
@Data
public class StaffDto implements Serializable {

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="员工类别")
    private Integer category;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="手机号码")
    private String mobile;

    @Schema(description="真实姓名")
    private String realName;

    @Schema(description="头像")
    private String avatar;

    @Schema(description="微信号")
    private String wechat;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="审核状态，A：审核通过；U：未审核；D：无效; ")
    private String auditedStatus;

    @Schema(description="审核时间")
    private Date auditedTime;

    @Schema(description="备注")
    private String description;

    @Schema(description="商户信息")
    private MtMerchant merchantInfo;

    @Schema(description="店铺信息")
    private MtStore storeInfo;

}
