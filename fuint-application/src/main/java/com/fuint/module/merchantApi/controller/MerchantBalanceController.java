package com.fuint.module.merchantApi.controller;

import com.fuint.common.dto.*;
import com.fuint.common.param.RechargeParam;
import com.fuint.common.service.*;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 余额接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="商户端-余额相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/balance")
public class MerchantBalanceController extends BaseController {

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 支付服务接口
     * */
    private PaymentService paymentService;

    /**
     * 充值余额
     * */
    @RequestMapping(value = "/doRecharge", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doRecharge(HttpServletRequest request, @RequestBody RechargeParam rechargeParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (null == userInfo) {
            return getFailureResult(1001);
        }

        MtOrder mtOrder = orderService.doRecharge(request, rechargeParam);
        Boolean result = false;
        if (mtOrder != null) {
            UserOrderDto orderInfo = orderService.getOrderByOrderSn(mtOrder.getOrderSn());
            if (orderInfo != null) {
                result = paymentService.paymentCallback(orderInfo);
            }
        }

        return getSuccessResult(result);
    }
}
