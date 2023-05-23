package com.fuint.module.clientApi.controller;

import com.fuint.common.param.SettlementParam;
import com.fuint.common.service.SettlementService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 结算中心接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-订单结算相关接口")
@RestController
@RequestMapping(value = "/clientApi/settlement")
public class ClientSettlementController extends BaseController {

    /**
     * 会员等级接口
     * */
    @Autowired
    private SettlementService settlementService;

    /**
     * 订单结算
     */
    @ApiOperation(value = "提交订单结算")
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody SettlementParam param) {
        try {
            Map<String, Object> result = settlementService.doSubmit(request, param);
            return getSuccessResult(result);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }
    }
}
