package com.fuint.common.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 后台登录账号信息
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class AccountInfo implements Serializable {

    @Schema(description="账户主键id")
    private Integer id;

    @Schema(description="账户编码")
    private String accountKey;

    @Schema(description="账户名称")
    private String accountName;

    @Schema(description="状态 : 0 无效 1 有效")
    private int accountStatus;

    @Schema(description="激活状态 : 0 未激活 1已激活")
    private String isActive;

    @Schema(description="创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @Schema(description="修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyDate;

    @Schema(description="随机码（公盐）")
    private String salt;

    @Schema(description="所属角色ID")
    private String roleIds;

    @Schema(description="是否被锁定")
    private int locked;

    @Schema(description="从属对象")
    private int ownerId;

    @Schema(description="真实姓名")
    private String realName;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属商户名称")
    private String merchantName;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="所属店铺名称")
    private String storeName;

    @Schema(description="关联员工ID")
    private Integer staffId;

    @Schema(description="登录的Token")
    private String token;

}
