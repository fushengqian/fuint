package com.fuint.application.web.rest;

import com.fuint.application.service.weixin.WeixinService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.service.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付类controller
 * Created by zach on 2021/05/7.
 */
@RestController
@RequestMapping(value = "/rest/pay")
public class PayController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    @Autowired
    private TokenService tokenService;

    /**
     * 微信支付服务接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 提交支付
     */
    @RequestMapping(value = "/doPay", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject doPay(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        ResponseObject responseObject = weixinService.createPrepayOrder(userInfo, "999", "预存卡", 100, 0);

        return getSuccessResult(responseObject.getData());
    }
}
