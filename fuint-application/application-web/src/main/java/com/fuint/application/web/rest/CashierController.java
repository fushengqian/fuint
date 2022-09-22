package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.member.MemberService;
import com.fuint.base.shiro.ShiroUser;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 收银台controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/cashier")
public class CashierController extends BaseController {

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

        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser == null) {
            return getFailureResult(1001);
        }

        MtUser userInfo = memberService.queryMemberByMobile(mobile);

        Map<String, Object> outParams = new HashMap<>();

        outParams.put("memberInfo", userInfo);

        return getSuccessResult(outParams);
    }
}
