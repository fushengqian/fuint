package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 后台管理员表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("t_account")
@Schema(name="TAccount对象", description="后台管理员表")
public class TAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="主键id")
    @TableId(value = "acct_id", type = IdType.AUTO)
    private Integer acctId;

    @Schema(description="账户编码")
    private String accountKey;

    @Schema(description="账户名称")
    private String accountName;

    @Schema(description="密码")
    private String password;

    @Schema(description="0 无效 1 有效")
    private Integer accountStatus;

    @Schema(description="0 未激活 1已激活")
    private Integer isActive;

    @Schema(description="创建时间")
    private Date createDate;

    @Schema(description="修改时间")
    private Date modifyDate;

    @Schema(description="随机码")
    private String salt;

    private String roleIds;

    private Integer locked;

    @Schema(description="所属平台")
    private Integer ownerId;

    private String realName;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="员工ID")
    private Integer staffId;
}
