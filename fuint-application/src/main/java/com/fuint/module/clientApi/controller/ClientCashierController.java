package com.fuint.module.clientApi.controller;

import com.fuint.common.service.MemberService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 收银台controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-收银台相关接口")
@RestController
@RequestMapping(value = "/clientApi/cashier")
public class ClientCashierController extends BaseController {

    @Autowired
    private MemberService memberService;

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/memberInfo", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject memberInfo(HttpServletRequest request) throws BusinessCheckException {
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        if (StringUtil.isEmpty(mobile)) {
            return getFailureResult(201);
        }

        MtUser userInfo = memberService.queryMemberByMobile(mobile);

        Map<String, Object> outParams = new HashMap<>();

        outParams.put("memberInfo", userInfo);

        return getSuccessResult(outParams);
    }
}
