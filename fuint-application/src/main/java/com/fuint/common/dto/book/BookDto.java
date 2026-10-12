package com.fuint.common.dto.book;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fuint.common.dto.common.DayDto;
import com.fuint.common.dto.common.TimeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预约实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description="自增ID")
    private Integer id;

    @Schema(description="预约名称")
    private String name;

    @Schema(description="所属商户ID")
    private Integer merchantId;

    @Schema(description="所属店铺ID")
    private Integer storeId;

    @Schema(description="预约类型")
    private String type;

    @Schema(description="图片地址")
    private String logo;

    @Schema(description="关联商品ID")
    private Integer goodsId;

    @Schema(description="类别ID")
    private Integer cateId;

    @Schema(description="可预约日期")
    private String serviceDates;

    @Schema(description="可预约日期，字符串")
    private String dates = "";

    @Schema(description="可预约日期列表")
    private List<DayDto> dateList;

    @Schema(description="可预约时间段")
    private String serviceTimes;

    @Schema(description="可预约时间段列表")
    private List<TimeDto> timeList;

    @Schema(description="可预约可预约时段")
    private List<BookTimeDto> times;

    @Schema(description="可预约员工ID")
    private String serviceStaffIds;

    @Schema(description="描述")
    private String description;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="更新时间")
    private Date updateTime;

    @Schema(description="最后操作人")
    private String operator;

    @Schema(description="排序")
    private Integer sort;

    @Schema(description="A：正常；D：删除")
    private String status;

}
