package com.fuint.coupon.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.coupon.ResponseObject;
import com.fuint.coupon.BaseController;
import com.fuint.coupon.dao.entities.MtUser;
import com.fuint.coupon.dao.entities.MtVerifyCode;
import com.fuint.coupon.dto.TokenDto;
import com.fuint.coupon.enums.StatusEnum;
import com.fuint.coupon.service.member.MemberService;
import com.fuint.coupon.service.token.TokenService;
import com.fuint.coupon.service.verifycode.VerifyCodeService;
import com.fuint.coupon.util.PhoneFormatCheckUtils;
import nl.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

/**
 * 登录类controller
 * Created by zach on 2019/7/16.
 */
@RestController
@RequestMapping(value = "/rest/sign")
public class SignController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    /**
     * 会员用户信息管理服务接口
     */
    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenService tokenService;

    /**
     * 验证码信息管理接口
     */
    @Autowired
    private VerifyCodeService verifyCodeService;

    /**
     * 会员验证码登录接口
     */
    @RequestMapping(value = "/doSign", method = RequestMethod.POST)
    public ResponseObject doLogin(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String mobile = request.getParameter("mobile");
        String verifyCode = request.getParameter("verifyCode");
        String usertoken = request.getHeader("token");

        TokenDto dto = new TokenDto();
        // 如果已经登录，免输入验证码
        if (tokenService.checkTokenLogin(usertoken)) {
            dto.setIsLogin("true");
            dto.setToken(usertoken);
            return getSuccessResult(JSONObject.toJSONString(dto));
        } else if (StringUtils.isEmpty(mobile)) {
            return getFailureResult(1002,"手机号码不能为空");
        } else if(!PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
            return getFailureResult(1002, "手机号码格式不正确");
        }

        MtUser mtUser=memberService.queryMemberByMobile(mobile);
        // 1,验证码验证
        MtVerifyCode mtVerifyCode= verifyCodeService.checkVerifyCode(mobile,verifyCode);
        // 2,写入token redis session

        if (mtUser != null && mtVerifyCode!=null) {
            if(!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                return getFailureResult(1002, mobile+"账号异常!");
            }

            // 更新验证码
            verifyCodeService.updateValidFlag(mtVerifyCode.getId(),"1");
            String userAgent = request.getHeader("user-agent");
            String token = tokenService.generateToken(userAgent, mobile);
            tokenService.saveToken(token, mtUser);

            dto.setIsLogin("true");
            dto.setToken(token);
            dto.setTokenCreatedTime(System.currentTimeMillis());
        } else {
            dto.setIsLogin("false");
            return getFailureResult(1002, "验证码错误，登录失败!");
        }

        return getSuccessResult(dto);
    }

    /**
     * 会员token获取会员信息
     */
    @RequestMapping(value = "/doGetUserInfo", method = RequestMethod.POST)
    public ResponseObject doGetUserInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String usertoken = request.getHeader("token");
        MtUser mtUser=tokenService.getUserInfoByToken(usertoken);
        if (mtUser==null) {
            return getFailureResult(1001, "用户没登录!");
        }
        return getSuccessResult(mtUser);
    }


    /**
     * 会员退出设备登录
     */
    @RequestMapping(value = "/doLogout", method = RequestMethod.POST)
    public ResponseObject doLogout(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String usertoken = request.getHeader("token");
        String userAgentStr = request.getHeader("user-agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        String direct;

        if (userAgent.getOperatingSystem().isMobileDevice()) {
            direct="MOBILE";
        } else {
            direct="PC";
        }

        Boolean flag=tokenService.removeTokenLikeMobile(usertoken,direct);
        if (Boolean.FALSE==flag) {
            return getFailureResult(1001, "退出错误!");
        } else {
            return getSuccessResult("退出成功！");
        }
    }
}
