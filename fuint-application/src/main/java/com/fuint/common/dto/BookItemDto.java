package com.fuint.common.dto;

import java.io.Serializable;
import java.util.Date;

import com.fuint.repository.model.MtStore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 预约订单Dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookItemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增ID")
    private Integer id;

    @ApiModelProperty("所属商户ID")
    private Integer merchantId;

    @ApiModelProperty("所属店铺ID")
    private Integer storeId;

    @ApiModelProperty("所属店铺信息")
    private MtStore storeInfo;

    @ApiModelProperty("预约分类ID")
    private Integer cateId;

    @ApiModelProperty("预约项目ID")
    private Integer bookId;

    @ApiModelProperty("预约项目名称")
    private String bookName;

    @ApiModelProperty("预约用户ID")
    private Integer userId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("核销码")
    private String verifyCode;

    @ApiModelProperty("预约联系人")
    private String contact;

    @ApiModelProperty("预约手机号")
    private String mobile;

    @ApiModelProperty("预约日期")
    private String serviceDate;

    @ApiModelProperty("预约时间段")
    private String serviceTime;

    @ApiModelProperty("预约备注")
    private String remark;

    @ApiModelProperty("预约员工ID")
    private Integer serviceStaffId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("最后操作人")
    private String operator;

    @ApiModelProperty("A：已提交；B：审核通过；C：审核未通过；D：删除；E：已完成")
    private String status;

    @ApiModelProperty("状态")
    private String statusName;

}
