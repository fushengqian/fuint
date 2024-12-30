package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色权限对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_duty_source")
@ApiModel(value = "TDutySource对象", description = "角色权限对象")
public class TDutySource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "duty_source_id", type = IdType.AUTO)
    private Integer dutySourceId;

    private Integer dutyId;

    private Integer sourceId;

}
