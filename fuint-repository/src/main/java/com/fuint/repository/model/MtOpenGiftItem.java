package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 开卡赠礼明细表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_open_gift_item")
@Schema(name="MtOpenGiftItem对象", description="开卡赠礼明细表")
public class MtOpenGiftItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="会用ID")
    private Integer userId;

    @Schema(description="赠礼ID")
    private Integer openGiftId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="状态")
    private String status;


}
