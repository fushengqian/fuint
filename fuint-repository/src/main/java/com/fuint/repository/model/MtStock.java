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
 * 库存管理记录表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_stock")
@ApiModel(value = "MtStock对象", description = "库存管理记录表")
public class MtStock implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("商户ID")
    private Integer merchantId;

    @ApiModelProperty("店铺ID")
    private Integer storeId;

    @ApiModelProperty("类型，increase:入库，reduce:出库")
    private String type;

    @ApiModelProperty("备注说明")
    private String description;

    @ApiModelProperty("赠送时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("状态，A正常；C取消")
    private String status;

}
