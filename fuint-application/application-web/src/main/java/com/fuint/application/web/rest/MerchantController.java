package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtStaff;
import com.fuint.application.service.staff.StaffService;
import com.fuint.application.service.confirmlog.ConfirmLogService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.util.DateUtil;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.service.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 商家相关controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/merchant")
public class MerchantController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MyCouponController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private ConfirmLogService confirmLogService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TokenService tokenService;

    /**
     * 查询商户信息
     *
     * @param request  Request对象
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException {
        String userToken = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        if (null == userInfo) {
            return getFailureResult(1001, "用户未登录");
        }

        userInfo = memberService.queryMemberById(userInfo.getId());
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("userInfo", userInfo);

        MtStaff confirmInfo = staffService.queryStaffByUserId(userInfo.getId());
        if (null == confirmInfo) {
            return getFailureResult(1002, "该账号不是商户");
        }

        outParams.put("confirmInfo", confirmInfo);

        // 收款额
        Date beginTime = DateUtil.getDayBegin();
        Date endTime = DateUtil.getDayEnd();
        BigDecimal payMoney = orderService.getPayMoney(confirmInfo.getStoreId(), beginTime, endTime);
        outParams.put("payMoney", payMoney);

        // 总会员数
        Long userCount = memberService.getUserCount(confirmInfo.getStoreId());
        outParams.put("userCount", userCount);

        // 今日订单数
        BigDecimal orderCount = orderService.getOrderCount(confirmInfo.getStoreId(), beginTime, endTime);
        outParams.put("orderCount", orderCount);

        // 核销券数
        Long confirmCount = confirmLogService.getConfirmCount(confirmInfo.getStoreId(), beginTime, endTime);
        outParams.put("couponCount", confirmCount);

        // 今日活跃会员数
        Long todayUser = memberService.getActiveUserCount(confirmInfo.getStoreId(), beginTime, endTime);
        outParams.put("todayUser", todayUser);

        return getSuccessResult(outParams);
    }
}
