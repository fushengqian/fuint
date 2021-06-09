package com.fuint.application.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.entities.MtVerifyCode;
import com.fuint.application.dto.TokenDto;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.verifycode.VerifyCodeService;
import com.fuint.application.util.PhoneFormatCheckUtils;
import nl.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录类controller
 * Created by zach on 2019/07/16.
 * Updated by zach on 2021/04/25.
 */
@RestController
@RequestMapping(value = "/rest/sign")
public class SignController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    /**
     * 会员服务接口
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
     * 微信授权登录
     * */
    @RequestMapping(value = "/mpWxLogin", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject mpWxLogin(HttpServletRequest request, @RequestBody Map<String, Object> param, Model model) throws BusinessCheckException{
        Map<String, Object> outParams = new HashMap<String, Object>();

        MtUser mtUser = memberService.queryMemberById(1);

        String userAgent = request.getHeader("user-agent");
        String token = tokenService.generateToken(userAgent, mtUser.getMobile());
        tokenService.saveToken(token, mtUser);

        outParams.put("token", token);
        outParams.put("userId", mtUser.getId());
        outParams.put("userName", mtUser.getRealName());

        return getSuccessResult("登录成功", outParams);
    }

    /**
     * 短信验证码登录
     */
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject signIn(HttpServletRequest request, @RequestBody Map<String, Object> param, Model model) throws BusinessCheckException{
        String mobile = param.get("mobile").toString();
        String verifyCode = param.get("verifyCode").toString();
        String userToken = request.getHeader("Access-Token");

        TokenDto dto = new TokenDto();
        // 如果已经登录，免输入验证码
        if (tokenService.checkTokenLogin(userToken) && StringUtils.isNotEmpty(userToken)) {
            dto.setIsLogin("true");
            dto.setToken(userToken);
            return getSuccessResult(JSONObject.toJSONString(dto));
        } else if (StringUtils.isEmpty(mobile)) {
            return getFailureResult(1002,"手机号码不能为空");
        } else if(!PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
            return getFailureResult(1002, "手机号码格式不正确");
        }

        MtUser mtUser = memberService.queryMemberByMobile(mobile);

        // 1、验证码验证
        MtVerifyCode mtVerifyCode = verifyCodeService.checkVerifyCode(mobile,verifyCode);

        // 2、写入token redis session
        if (mtUser != null && mtVerifyCode != null) {
            if (!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                return getFailureResult(1002, "账号异常，登录失败");
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
            return getFailureResult(1002, "验证码错误，登录失败");
        }


        Map<String, Object> outParams = new HashMap<String, Object>();

        outParams.put("token", dto.getToken());
        outParams.put("userId", mtUser.getId());
        outParams.put("userName", mtUser.getRealName());

        return getSuccessResult("登录成功", outParams);
    }

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/doGetUserInfo", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGetUserInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);

        if (mtUser == null) {
            return getFailureResult(1001, "用户没登录!");
        }

        return getSuccessResult(mtUser);
    }

    /**
     * 会员退出登录
     */
    @RequestMapping(value = "/signOut", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doLogout(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        String userAgentStr = request.getHeader("user-agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        String direct;

        if (userAgent.getOperatingSystem().isMobileDevice()) {
            direct = "MOBILE";
        } else {
            direct = "PC";
        }

        Boolean flag = tokenService.removeTokenLikeMobile(userToken, direct);
        if (Boolean.FALSE == flag) {
            return getFailureResult(1001, "退出错误!");
        } else {
            return getSuccessResult("退出成功！");
        }
    }
}
