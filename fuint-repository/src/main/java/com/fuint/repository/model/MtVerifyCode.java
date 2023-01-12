package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 短信验证码表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_verify_code")
@ApiModel(value = "MtVerifyCode对象", description = "短信验证码表")
public class MtVerifyCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("验证码")
    private String verifyCode;

    @ApiModelProperty("创建时间")
    private Date addTime;

    @ApiModelProperty("过期时间")
    private Date expireTime;

    @ApiModelProperty("使用时间")
    private Date usedTime;

    @ApiModelProperty("可用状态 0未用 1已用 2置为失效")
    private String validFlag;


}
