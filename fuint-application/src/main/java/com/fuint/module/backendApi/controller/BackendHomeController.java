package com.fuint.module.backendApi.controller;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.service.AccountService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TimeUtils;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.TAccount;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import java.math.BigDecimal;
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
@RequestMapping(value = "/backendApi/home")
public class BackendHomeController extends BaseController {

    @Resource
    private MemberService memberService;

    @Resource
    private OrderService orderService;

    @Resource
    private AccountService accountService;

    /**
     * 首页数据
     *
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject index(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        Date beginTime = DateUtil.getDayBegin();
        Date endTime = DateUtil.getDayEnd();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Integer storeId = accountInfo.getStoreId();

        // 总会员数
        Long totalUser = memberService.getUserCount(storeId);
        // 今日新增会员数量
        Long todayUser = memberService.getUserCount(storeId, beginTime, endTime);

        // 总订单数
        BigDecimal totalOrder = orderService.getOrderCount(storeId);
        // 今日订单数
        BigDecimal todayOrder = orderService.getOrderCount(storeId, beginTime, endTime);

        // 今日交易金额
        BigDecimal todayPay = orderService.getPayMoney(storeId, beginTime, endTime);
        // 总交易金额
        BigDecimal totalPay = orderService.getPayMoney(storeId);

        // 今日活跃会员数
        Long todayActiveUser = memberService.getActiveUserCount(storeId, beginTime, endTime);

        // 总支付人数
        Integer totalPayUser = orderService.getPayUserCount(storeId);

        Map<String, Object> result = new HashMap<>();

        result.put("todayUser", todayUser);
        result.put("totalUser", totalUser);
        result.put("todayOrder", todayOrder);
        result.put("totalOrder", totalOrder);
        result.put("todayPay", todayPay);
        result.put("totalPay", totalPay);
        result.put("todayActiveUser", todayActiveUser);
        result.put("totalPayUser", totalPayUser);

        return getSuccessResult(result);
    }

    /**
     * 首页图表统计数据
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject statistic(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String tag = request.getParameter("tag") == null ? "order,user_active" : request.getParameter("tag");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId();

        ArrayList<String> days = TimeUtils.getDays(5);
        days.add("昨天");
        days.add("今天");

        Map<String, Object> result = new HashMap<>();
        if (tag.equals("payment")) {
            BigDecimal[] orderPayData = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            for (int i = 0; i < 7; i++) {
                Date beginTime = DateUtil.getDayBegin((6 - i));
                Date endTime = DateUtil.getDayEnd((6 - i));
                BigDecimal payMoney = orderService.getPayMoney(storeId, beginTime, endTime);
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
                orderCountData[i] = orderService.getOrderCount(storeId, beginTime, endTime);
                Long userCount = memberService.getActiveUserCount(storeId, beginTime, endTime);
                userCountData[i] = new BigDecimal(userCount);
            }
            BigDecimal data[][] = { orderCountData, userCountData };
            result.put("data", data);
        }

        result.put("labels", days);

        return getSuccessResult(result);
    }

    /**
     * 收款结果页面
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/cashierResult", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cashierResult(HttpServletRequest request) throws BusinessCheckException {
        Integer orderId = request.getParameter("orderId") == null ? 0 : Integer.parseInt(request.getParameter("orderId"));

        UserOrderDto orderInfo = orderService.getOrderById(orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);

        return getSuccessResult(result);
    }
}
