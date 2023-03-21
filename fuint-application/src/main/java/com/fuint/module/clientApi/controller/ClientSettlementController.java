package com.fuint.module.clientApi.controller;

import com.fuint.common.service.SettlementService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
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
@RestController
@RequestMapping(value = "/clientApi/settlement")
public class ClientSettlementController extends BaseController {

    /**
     * 会员等级接口
     * */
    @Autowired
    SettlementService settlementService;

    /**
     * 结算提交
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        try {
            Map<String, Object> result = settlementService.doSubmit(request, param);
            return getSuccessResult(result);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, e.getMessage());
        }
    }
}
