package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员标签关联实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_user_tag_relation")
@Schema(name="MtUserTagRelation对象", description="会员标签关联表")
public class MtUserTagRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="会员ID")
    private Integer userId;

    @Schema(description="标签ID")
    private Integer tagId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="操作人")
    private String operator;
}
