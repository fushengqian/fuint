package com.fuint.module.clientApi.controller;

import com.fuint.common.param.SettlementParam;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.OrderService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.utils.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 结算中心接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name="会员端-订单结算相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/settlement")
public class ClientSettlementController extends BaseController {

    /**
     * 订单接口
     * */
    private OrderService orderService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    /**
     * 订单结算
     */
    @Operation(summary="提交订单结算")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody SettlementParam param) throws BusinessCheckException {
        // 校验商户是否已过期
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        if (StringUtil.isNotEmpty(merchantNo)) {
            Integer merchantId = merchantService.getMerchantId(merchantNo);
            merchantService.checkMerchantValid(merchantId);
        }
        Map<String, Object> result = orderService.doSettle(request, param);
        return getSuccessResult(result);
    }
}
