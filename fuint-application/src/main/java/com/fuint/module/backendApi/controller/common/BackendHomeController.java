package com.fuint.module.backendApi.controller.common;

import com.fuint.common.dto.order.UserOrderDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.ReportService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TimeUtils;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="管理端-首页相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/home")
public class BackendHomeController extends BaseController {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 报表服务接口
     * */
    private ReportService reportService;

    /**
     * 首页统计数据
     */
    @ApiOperation(value = "首页统计数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject index() throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        String startTime = DateUtil.formatDate(DateUtil.getDayBegin(), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.formatDate(DateUtil.getDayEnd(), "yyyy-MM-dd HH:mm:ss");

        Map<String, Object> data = reportService.getReportOverview(accountInfo.getMerchantId(), accountInfo.getStoreId(), startTime, endTime);

        Map<String, Object> result = new HashMap<>();
        result.put("todayUser", data.get("userCount"));
        result.put("totalUser", data.get("totalUserCount"));
        result.put("todayOrder", data.get("orderCount"));
        result.put("totalOrder", data.get("totalOrderCount"));
        result.put("todayPay", data.get("payAmount"));
        result.put("totalPay", data.get("totalPayAmount"));
        result.put("todayActiveUser", data.get("activeUserCount"));
        result.put("totalPayUser", data.get("totalPayUserCount"));

        return getSuccessResult(result);
    }

    /**
     * 首页图表统计数据
     */
    @ApiOperation(value = "首页图表统计数据")
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject statistic(HttpServletRequest request) {
        String tag = request.getParameter("tag") == null ? "order,user_active" : request.getParameter("tag");
        Integer storeId = StringUtil.isEmpty(request.getParameter("storeId")) ? 0 : Integer.parseInt(request.getParameter("storeId"));

        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();

        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId();
        }

        ArrayList<String> days = TimeUtils.getDays(5);
        days.add("昨天");
        days.add("今天");

        Map<String, Object> result = new HashMap<>();
        if (tag.equals("payment")) {
            BigDecimal[] orderPayData = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            for (int i = 0; i < 7; i++) {
                Date beginTime = DateUtil.getDayBegin((6 - i));
                Date endTime = DateUtil.getDayEnd((6 - i));
                BigDecimal payMoney = orderService.getPayMoney(merchantId, storeId, beginTime, endTime);
                orderPayData[i] = payMoney == null ? new BigDecimal("0") : payMoney;
            }
            BigDecimal data[][] = { orderPayData };
            result.put("data", data);
        } else {
            BigDecimal[] orderCountData = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            BigDecimal[] userCountData = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};

            for (int i = 0; i < 7; i++) {
                 Date beginTime = DateUtil.getDayBegin((6 - i));
                 Date endTime = DateUtil.getDayEnd((6 - i));
                 orderCountData[i] = orderService.getOrderCount(merchantId, storeId, beginTime, endTime);
                 Long userCount = memberService.getActiveUserCount(merchantId, storeId, beginTime, endTime);
                 userCountData[i] = new BigDecimal(userCount);
            }
            BigDecimal data[][] = { orderCountData, userCountData };
            result.put("data", data);
        }

        result.put("labels", days);

        return getSuccessResult(result);
    }

    /**
     * 获取收款结果
     */
    @ApiOperation(value = "获取收款结果")
    @RequestMapping(value = "/cashierResult", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cashierResult(HttpServletRequest request) {
        Integer orderId = request.getParameter("orderId") == null ? 0 : Integer.parseInt(request.getParameter("orderId"));

        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);

        return getSuccessResult(result);
    }
}
