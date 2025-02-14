package com.fuint.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员详情请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class MemberDetailParam implements Serializable {

    @ApiModelProperty(value="会员ID", name="memberId")
    private Integer memberId;

}
