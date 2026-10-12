package com.fuint.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 预约提交请求参数
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class BookSubmitParam implements Serializable {

    @Schema(description="预约ID", name="bookId")
    private String bookId;

    @Schema(description="订单商品ID", name="orderGoodsId")
    private String orderGoodsId;

    @Schema(description="员工ID", name="staffId")
    private String staffId;

    @Schema(description="备注", name="remark")
    private String remark;

    @Schema(description="手机号", name="mobile")
    private String mobile;

    @Schema(description="联系人", name="contact")
    private String contact;

    @Schema(description="预约日期", name="date")
    private String date;

    @Schema(description="预约时间", name="time")
    private String time;

}
