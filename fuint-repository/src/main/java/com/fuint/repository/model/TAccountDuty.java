package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 后台账号角色表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_account_duty")
@ApiModel(value = "TAccountDuty对象", description = "后台账号角色表")
public class TAccountDuty implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("账户角色ID")
    @TableId(value = "acc_duty_id", type = IdType.AUTO)
    private Integer accDutyId;

    @ApiModelProperty("账户ID")
    private Integer acctId;

    @ApiModelProperty("角色ID")
    private Integer dutyId;


}
