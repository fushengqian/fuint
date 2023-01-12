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
 * <p>
 * 开卡赠礼明细表
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_open_gift_item")
@ApiModel(value = "MtOpenGiftItem对象", description = "开卡赠礼明细表")
public class MtOpenGiftItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("会用ID")
    private Integer userId;

    @ApiModelProperty("赠礼ID")
    private Integer openGiftId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("状态")
    private String status;


}
