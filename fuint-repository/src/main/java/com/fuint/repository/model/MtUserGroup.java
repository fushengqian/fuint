package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员分组
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user_group")
@Schema(name="MtUserGroup对象", description="会员分组")
public class MtUserGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="分组ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="分组名称")
    private String name;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="默认店铺")
    private Integer storeId;

    @Schema(description="父ID")
    private Integer parentId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="状态，A：激活；N：禁用；D：删除")
    private String status;

    @Schema(description="备注信息")
    private String description;

    @Schema(description="最后操作人")
    private String operator;
}
