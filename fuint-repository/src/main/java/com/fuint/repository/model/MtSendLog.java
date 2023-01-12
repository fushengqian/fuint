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
 * 卡券发放记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_send_log")
@ApiModel(value = "MtSendLog对象", description = "卡券发放记录表")
public class MtSendLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("1：单用户发券；2：批量发券")
    private Integer type;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("导入excel文件名")
    private String fileName;

    @ApiModelProperty("导入excel文件路径")
    private String filePath;

    @ApiModelProperty("用户手机")
    private String mobile;

    @ApiModelProperty("券组ID")
    private Integer groupId;

    @ApiModelProperty("券组名称")
    private String groupName;

    @ApiModelProperty("卡券ID")
    private Integer couponId;

    @ApiModelProperty("发放套数")
    private Integer sendNum;

    @ApiModelProperty("操作时间")
    private Date createTime;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("导入UUID")
    private String uuid;

    @ApiModelProperty("作废成功张数")
    private Integer removeSuccessNum;

    @ApiModelProperty("作废失败张数")
    private Integer removeFailNum;

    @ApiModelProperty("状态，A正常；B：部分作废；D全部作废")
    private String status;


}
