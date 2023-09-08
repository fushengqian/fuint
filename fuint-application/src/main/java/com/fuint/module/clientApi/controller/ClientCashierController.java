package com.fuint.module.clientApi.controller;

import com.fuint.common.param.MemberInfoParam;
import com.fuint.common.service.MemberService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    /**
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 获取会员信息
     */
    @ApiOperation(value = "查询会员信息")
    @RequestMapping(value = "/memberInfo", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject memberInfo(@RequestBody MemberInfoParam memberInfoParam) throws BusinessCheckException {
        String mobile = memberInfoParam.getMobile() == null ? "" : memberInfoParam.getMobile();
        if (StringUtil.isEmpty(mobile)) {
            return getFailureResult(201);
        }

        MtUser userInfo = memberService.queryMemberByMobile(mobile);
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("memberInfo", userInfo);

        return getSuccessResult(outParams);
    }
}
