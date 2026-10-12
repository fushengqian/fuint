package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 店铺员工表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_staff")
@Schema(name="MtStaff对象", description="店铺员工表")
public class MtStaff implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
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

}
