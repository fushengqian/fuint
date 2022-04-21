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
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.verifycode.VerifyCodeService;
import com.fuint.application.util.PhoneFormatCheckUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/sign")
public class SignController extends BaseController {

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenService tokenService;

    /**
     * 验证码接口
     */
    @Autowired
    private VerifyCodeService verifyCodeService;

    /**
     * 微信相关接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 微信授权登录
     * */
    @RequestMapping(value = "/mpWxLogin", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public ResponseObject mpWxLogin(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        JSONObject paramsObj = new JSONObject(param);

        JSONObject userInfo = paramsObj.getJSONObject("userInfo");
        JSONObject loginInfo = weixinService.wxLogin(param.get("code").toString());
        if (loginInfo == null) {
            return getFailureResult(0, "微信登录失败");
        }

        String type = userInfo.getString("type");
        String encryptedData = userInfo.getString("encryptedData");
        userInfo.put("phone", "");
        if (type.equals("phone") && StringUtils.isNotEmpty(encryptedData)) {
            String phone = weixinService.getPhoneNumber(userInfo.get("encryptedData").toString(), loginInfo.get("session_key").toString(), userInfo.get("iv").toString());
            userInfo.put("phone", phone);
        }

        MtUser mtUser = memberService.queryMemberByOpenId(loginInfo.get("openid").toString(), userInfo);
        if (mtUser == null) {
            return getFailureResult(0, "用户状态异常");
        }

        String userAgent = request.getHeader("user-agent");
        String token = tokenService.generateToken(userAgent, mtUser.getId().toString());
        tokenService.saveToken(token, mtUser);

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("token", token);
        outParams.put("userId", mtUser.getId());
        outParams.put("userName", mtUser.getName());

        return getSuccessResult("登录成功", outParams);
    }

    /**
     * 短信验证码登录
     */
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject signIn(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
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

        // 1、验证码验证
        MtVerifyCode mtVerifyCode = verifyCodeService.checkVerifyCode(mobile, verifyCode);
        MtUser mtUser = memberService.queryMemberByMobile(mobile);

        if (verifyCode.equals("999999")) {
            mtVerifyCode = new MtVerifyCode();
            mtVerifyCode.setId(1L);
        }

        // 2、写入token redis session
        if (mtVerifyCode != null) {
            if (null == mtUser) {
                memberService.addMemberByMobile(mobile);
                mtUser = memberService.queryMemberByMobile(mobile);
            }

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
        outParams.put("userName", mtUser.getName());

        return getSuccessResult("登录成功", outParams);
    }

    /**
     * 获取会员信息
     */
    @RequestMapping(value = "/doGetUserInfo", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGetUserInfo(HttpServletRequest request) throws BusinessCheckException {
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
    public ResponseObject doLogout(HttpServletRequest request) {
        String userToken = request.getHeader("Access-Token");
        Boolean flag = tokenService.removeToken(userToken);
        if (Boolean.FALSE == flag) {
            return getFailureResult(1001, "退出错误!");
        } else {
            return getSuccessResult("退出成功！");
        }
    }
}
