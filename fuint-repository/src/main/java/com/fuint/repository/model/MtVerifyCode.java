package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 短信验证码表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_verify_code")
@Schema(name="MtVerifyCode对象", description="短信验证码表")
public class MtVerifyCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description="手机号")
    private String mobile;

    @Schema(description="验证码")
    private String verifyCode;

    @Schema(description="创建时间")
    private Date addTime;

    @Schema(description="过期时间")
    private Date expireTime;

    @Schema(description="使用时间")
    private Date usedTime;

    @Schema(description="可用状态 0未用 1已用 2置为失效")
    private String validFlag;


}
