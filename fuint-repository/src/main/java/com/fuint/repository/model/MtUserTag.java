package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员标签实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user_tag")
@Schema(name="MtUserTag对象", description="会员标签表")
public class MtUserTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="标签名称")
    private String name;

    @Schema(description="标签颜色")
    private String color;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="标签描述")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="状态，A：激活；N：禁用；D：删除")
    private String status;
}
