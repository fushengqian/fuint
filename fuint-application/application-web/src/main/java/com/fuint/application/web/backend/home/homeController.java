package com.fuint.application.web.backend.home;

import com.fuint.application.FrameworkConstants;
import com.fuint.application.ResponseObject;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.util.DateUtil;
import com.fuint.application.util.TimeUtils;
import com.fuint.base.dao.entities.TAccount;
import com.fuint.base.service.account.TAccountService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Controller
@RequestMapping(value = "/backend/home")
public class homeController {

    private static final Logger logger = LoggerFactory.getLogger(homeController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TAccountService accountService;

    /**
     * 首页
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Date beginTime = DateUtil.getDayBegin();
        Date endTime = DateUtil.getDayEnd();

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();

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

        model.addAttribute("todayUser", todayUser);
        model.addAttribute("totalUser", totalUser);
        model.addAttribute("todayOrder", todayOrder);
        model.addAttribute("totalOrder", totalOrder);
        model.addAttribute("todayPay", todayPay);
        model.addAttribute("totalPay", totalPay);
        model.addAttribute("todayActiveUser", todayActiveUser);
        model.addAttribute("totalPayUser", totalPayUser);

        return "home/index";
    }

    /**
     * 首页图表统计数据
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject statistic(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String tag = request.getParameter("tag") == null ? "order,user_active" : request.getParameter("tag");

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        TAccount account = accountService.findAccountById(shiroUser.getId());
        Integer storeId = account.getStoreId();

        ArrayList<String> days = TimeUtils.getDays(5);
        days.add("昨天");
        days.add("今天");

        Map<String, Object> resultMap = new HashMap<>();
        if (tag.equals("payment")) {
            BigDecimal[] orderPayData = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            for (int i = 0; i < 7; i++) {
                Date beginTime = DateUtil.getDayBegin((6 - i));
                Date endTime = DateUtil.getDayEnd((6 - i));
                BigDecimal payMoney = orderService.getPayMoney(storeId, beginTime, endTime);
                orderPayData[i] = payMoney == null ? new BigDecimal("0") : payMoney;
            }
            BigDecimal data[][] = { orderPayData };
            resultMap.put("data", data);
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
            resultMap.put("data", data);
        }

        resultMap.put("labels", days);

        return new ResponseObject(FrameworkConstants.HTTP_RESPONSE_CODE_SUCCESS, "请求成功", resultMap);
    }

    /**
     * 核销卡券
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/confirmCoupon")
    public String confirmCoupon(HttpServletRequest request, HttpServletResponse response, Model model) {
        // 核销码
        String code = request.getParameter("code") == null ? "": request.getParameter("code");
        model.addAttribute("code", code);

        return "home/confirmCoupon";
    }

    /**
     * 快速收款
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/toCashier")
    public String toCashier(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        return "home/toCashier";
    }

    /**
     * 收款结果页面
     *
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/cashierResult")
    public String cashierResult(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        Integer orderId = request.getParameter("orderId") == null ? 0 : Integer.parseInt(request.getParameter("orderId"));

        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        model.addAttribute("orderInfo", orderInfo);

        return "home/cashierResult";
    }
}
