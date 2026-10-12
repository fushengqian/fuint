package com.fuint.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预约订单实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
@TableName("mt_book_item")
@Schema(name="MtBookItem对象", description="MtBookItem表对象")
public class MtBookItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="预约分类ID")
    private Integer cateId;

    @Schema(description="预约ID")
    private Integer bookId;

    @Schema(description="预约用户ID")
    private Integer userId;

    @Schema(description="商品ID")
    private Integer goodsId;

    @Schema(description="核销码")
    private String verifyCode;

    @Schema(description="预约联系人")
    private String contact;

    @Schema(description="预约手机号")
    private String mobile;

    @Schema(description="预约日期")
    private String serviceDate;

    @Schema(description="预约时间段")
    private String serviceTime;

    @Schema(description="预约备注")
    private String remark;

    @Schema(description="预约员工ID")
    private Integer serviceStaffId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="A：已提交；B：审核通过；C：审核未通过；D：删除；E：已完成")
    private String status;

}
