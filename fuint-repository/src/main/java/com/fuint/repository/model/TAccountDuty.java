package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 后台账号角色表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("t_account_duty")
@Schema(name="TAccountDuty对象", description="后台账号角色表")
public class TAccountDuty implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="账户角色ID")
    @TableId(value = "acc_duty_id", type = IdType.AUTO)
    private Integer accDutyId;

    @Schema(description="账户ID")
    private Integer acctId;

    @Schema(description="角色ID")
    private Integer dutyId;

}
