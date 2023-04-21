package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.UserInfo;
import com.fuint.common.service.ConfirmLogService;
import com.fuint.common.service.MemberService;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.StaffService;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtStaff;
import com.fuint.repository.model.MtUser;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-商户信息相关接口")
@RestController
@RequestMapping(value = "/merchantApi/merchant")
public class MerchantController extends BaseController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private ConfirmLogService confirmLogService;

    @Autowired
    private OrderService orderService;

    /**
     * 查询商户信息
     *
     * @param request Request对象
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (null == userInfo) {
            return getFailureResult(1001, "用户未登录");
        }

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("userInfo", mtUser);

        MtStaff confirmInfo = staffService.queryStaffByMobile(mtUser.getMobile());
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
