package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 卡券发放记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_send_log")
@Schema(name="MtSendLog对象", description="卡券发放记录表")
public class MtSendLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="商户ID")
    private Integer merchantId;

    @Schema(description="店铺ID")
    private Integer storeId;

    @Schema(description="1：单用户发券；2：批量发券")
    private Integer type;

    @Schema(description="用户ID")
    private Integer userId;

    @Schema(description="导入excel文件名")
    private String fileName;

    @Schema(description="导入excel文件路径")
    private String filePath;

    @Schema(description="用户手机")
    private String mobile;

    @Schema(description="券组ID")
    private Integer groupId;

    @Schema(description="券组名称")
    private String groupName;

    @Schema(description="卡券ID")
    private Integer couponId;

    @Schema(description="发放套数")
    private Integer sendNum;

    @Schema(description="操作时间")
    private Date createTime;

    @Schema(description="操作人")
    private String operator;

    @Schema(description="导入UUID")
    private String uuid;

    @Schema(description="作废成功张数")
    private Integer removeSuccessNum;

    @Schema(description="作废失败张数")
    private Integer removeFailNum;

    @Schema(description="状态，A正常；B：部分作废；D全部作废")
    private String status;


}
