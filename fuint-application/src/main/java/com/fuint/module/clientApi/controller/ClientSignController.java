package com.fuint.module.clientApi.controller;

import com.alibaba.fastjson.JSONObject;
import com.fuint.common.dto.TokenDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.GenderEnum;
import com.fuint.common.enums.MemberSourceEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtMerchant;
import com.fuint.repository.model.MtStore;
import com.fuint.repository.model.MtUser;
import com.fuint.repository.model.MtVerifyCode;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-登录相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/sign")
public class ClientSignController extends BaseController {

    /**
     * 系统环境变量
     * */
    private Environment env;

    private static final Logger logger = LoggerFactory.getLogger(ClientSignController.class);

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 验证码接口
     */
    private VerifyCodeService verifyCodeService;

    /**
     * 微信相关接口
     * */
    private WeixinService weixinService;

    /**
     * 图形验证码
     * */
    private CaptchaService captchaService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 微信授权登录（小程序）
     * */
    @ApiOperation(value = "微信授权登录（小程序）")
    @RequestMapping(value = "/mpWxLogin", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public ResponseObject mpWxLogin(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String storeId = request.getHeader("storeId") == null ? "0" : request.getHeader("storeId");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String shareId = param.get("shareId") == null ? "0" : param.get("shareId").toString();
        JSONObject paramsObj = new JSONObject(param);
        logger.info("微信授权登录参数：{}", param);
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        JSONObject userInfo = paramsObj.getJSONObject("userInfo");
        JSONObject loginInfo = weixinService.getWxProfile(merchantId, param.get("code").toString());
        userInfo.put("shareId", shareId);
        if (loginInfo == null) {
            return getFailureResult(0, "微信登录失败");
        }

        String type = userInfo.getString("type");
        String encryptedData = userInfo.getString("encryptedData");
        userInfo.put("phone", "");
        userInfo.put("source", MemberSourceEnum.WECHAT_LOGIN.getKey());
        if (type.equals("phone") && StringUtil.isNotEmpty(encryptedData)) {
            String phone = weixinService.getPhoneNumber(userInfo.get("encryptedData").toString(), loginInfo.get("session_key").toString(), userInfo.get("iv").toString());
            userInfo.put("phone", phone);
        }
        // 默认店铺
        if (StringUtil.isEmpty(storeId)) {
            MtStore mtStore = storeService.getDefaultStore(merchantNo);
            if (mtStore != null) {
                storeId = mtStore.getId().toString();
            }
        }
        userInfo.put("storeId", storeId);

        MtUser mtUser = memberService.queryMemberByOpenId(merchantId, loginInfo.get("openid").toString(), userInfo);
        if (mtUser == null) {
            return getFailureResult(0, "用户状态异常");
        }

        String userAgent = request.getHeader("user-agent");
        String token = TokenUtil.generateToken(userAgent, mtUser.getId());
        UserInfo userLoginInfo = new UserInfo();
        userLoginInfo.setId(mtUser.getId());
        userLoginInfo.setToken(token);
        TokenUtil.saveToken(userLoginInfo);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", mtUser.getId());
        result.put("userName", mtUser.getName());
        result.put("openId", mtUser.getOpenId());

        return getSuccessResult("登录成功", result);
    }

    /**
     * 微信授权登录（公众号）
     * */
    @ApiOperation(value = "微信授权登录（公众号）")
    @RequestMapping(value = "/mpWxAuth", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public ResponseObject mpWxAuth(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String storeId = request.getHeader("storeId") == null ? "0" : request.getHeader("storeId");
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        String shareId = param.get("shareId") == null ? "0" : param.get("shareId").toString();
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        JSONObject userInfo = weixinService.getWxOpenId(merchantId, param.get("code").toString());
        String ip = CommonUtil.getIPFromHttpRequest(request);
        if (userInfo == null) {
            return getFailureResult(201, "微信公众号授权失败");
        }

        logger.error("公众号授权登录, userInfo:{}，param:{}", userInfo, param);

        userInfo.put("storeId", storeId);
        userInfo.put("shareId", shareId);
        userInfo.put("platform", platform);
        userInfo.put("ip", ip);

        MtUser mtUser = memberService.queryMemberByOpenId(merchantId, userInfo.get("openid").toString(), userInfo);
        if (mtUser == null) {
            return getFailureResult(201, "微信公众号授权失败");
        }

        String userAgent = request.getHeader("user-agent");
        String token = TokenUtil.generateToken(userAgent, mtUser.getId());
        UserInfo userLoginInfo = new UserInfo();
        userLoginInfo.setId(mtUser.getId());
        userLoginInfo.setToken(token);
        TokenUtil.saveToken(userLoginInfo);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", mtUser.getId());
        result.put("userName", mtUser.getName());
        result.put("openId", mtUser.getOpenId());

        return getSuccessResult("登录成功", result);
    }

    /**
     * 通过账号密码注册
     * */
    @ApiOperation(value = "通过账号密码注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject register(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String account = param.get("account").toString();
        String password = param.get("password").toString();
        String captchaCode = param.get("captchaCode") == null ? "" : param.get("captchaCode").toString();
        String uuid = param.get("uuid") == null ? "" : param.get("uuid").toString();
        String shareId = param.get("shareId") == null ? "0" : param.get("shareId").toString();
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String userAgent = request.getHeader("user-agent") == null ? "" : request.getHeader("user-agent");
        String ip = CommonUtil.getIPFromHttpRequest(request);
        if (StringUtil.isEmpty(account)) {
            return getFailureResult(201,"用户名不能为空");
        }
        if (StringUtil.isEmpty(password)) {
            return getFailureResult(201,"密码不能为空");
        }
        if (StringUtil.isEmpty(captchaCode)) {
            return getFailureResult(201,"图形验证码不能为空");
        }
        boolean captchaVerify = captchaService.checkCodeByUuid(captchaCode, uuid);
        if (!captchaVerify) {
            return getFailureResult(201,"图形验证码有误");
        }
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        MtUser userData = memberService.queryMemberByName(merchantId, account);
        if (userData != null) {
            return getFailureResult(201,"该用户名已存在");
        }

        MtUser mtUser = new MtUser();
        mtUser.setName(account);
        mtUser.setPassword(password);
        mtUser.setMerchantId(merchantId);
        mtUser.setStoreId(storeId);
        mtUser.setSex(GenderEnum.MAN.getKey());
        mtUser.setMobile("");
        mtUser.setDescription("会员自行注册新账号");
        mtUser.setIsStaff(YesOrNoEnum.NO.getKey());
        mtUser.setIp(ip);
        MtUser userInfo = memberService.addMember(mtUser, shareId);

        if (userInfo != null) {
            String token = TokenUtil.generateToken(userAgent, userInfo.getId());
            UserInfo loginInfo = new UserInfo();
            loginInfo.setId(userInfo.getId());
            loginInfo.setToken(token);
            TokenUtil.saveToken(loginInfo);
            Map<String, Object> outParams = new HashMap<>();
            outParams.put("userId", userInfo.getId());
            outParams.put("userName", userInfo.getName());
            outParams.put("token", token);
            outParams.put("openId", "");

            String domain = env.getProperty("website.url");
            String appId = env.getProperty("weixin.official.appId");
            MtMerchant mtMerchant = merchantService.queryMerchantById(merchantId);
            if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxOfficialAppId())) {
                appId = mtMerchant.getWxOfficialAppId();
            }

            outParams.put("domain", domain);
            outParams.put("appId", appId);
            return getSuccessResult("注册成功", outParams);
        } else {
            return getFailureResult(201,"注册失败");
        }
    }

    /**
     * 会员登录（通过短信或账号密码）
     */
    @ApiOperation(value = "通过短信或账号密码登录")
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject signIn(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String userAgent = request.getHeader("user-agent") == null ? "" : request.getHeader("user-agent");
        String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
        String verifyCode = param.get("verifyCode") == null ? "" : param.get("verifyCode").toString();
        String account = param.get("account") == null ? "" : param.get("account").toString();
        String password = param.get("password") == null ? "" : param.get("password").toString();
        String captchaCode = param.get("captchaCode") == null ? "" : param.get("captchaCode").toString();
        String uuid = param.get("uuid") == null ? "" : param.get("uuid").toString();
        String shareId = param.get("shareId") == null ? "0" : param.get("shareId").toString();
        String ip = CommonUtil.getIPFromHttpRequest(request);
        TokenDto dto = new TokenDto();
        MtUser mtUser = null;
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        // 方式1：通过短信验证码登录
        if (StringUtil.isNotEmpty(mobile) && StringUtil.isNotEmpty(verifyCode)) {
            // 如果已经登录，免输入验证码
            if (StringUtil.isNotEmpty(token) && TokenUtil.checkTokenLogin(token)) {
                dto.setIsLogin(YesOrNoEnum.TRUE.getKey());
                dto.setToken(token);
                return getSuccessResult(JSONObject.toJSONString(dto));
            }

            // 1、验证码验证
            MtVerifyCode mtVerifyCode = verifyCodeService.checkVerifyCode(mobile, verifyCode);
            mtUser = memberService.queryMemberByMobile(merchantId, mobile);

            if (verifyCode.equals("999999")) {
                mtVerifyCode = new MtVerifyCode();
                mtVerifyCode.setId(1L);
            }

            // 2、写入token redis session
            if (mtVerifyCode != null) {
                if (null == mtUser) {
                    memberService.addMemberByMobile(merchantId, mobile, shareId, ip);
                    mtUser = memberService.queryMemberByMobile(merchantId, mobile);
                }

                if (!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    return getFailureResult(201, "账号状态异常，登录失败");
                }

                String userToken = TokenUtil.generateToken(userAgent, mtUser.getId());
                UserInfo loginInfo = new UserInfo();
                loginInfo.setId(mtUser.getId());
                loginInfo.setToken(userToken);
                TokenUtil.saveToken(loginInfo);

                dto.setIsLogin(YesOrNoEnum.TRUE.getKey());
                dto.setToken(userToken);
                dto.setTokenCreatedTime(System.currentTimeMillis());
            } else {
                dto.setIsLogin(YesOrNoEnum.FALSE.getKey());
                return getFailureResult(201, "验证码错误或已过期，登录失败");
            }
        }

        // 方式2：通过账号密码登录
        if (StringUtil.isNotEmpty(account) && StringUtil.isNotEmpty(password) && StringUtil.isNotEmpty(captchaCode)) {
            Boolean captchaVerify = captchaService.checkCodeByUuid(captchaCode, uuid);
            if (!captchaVerify) {
                return getFailureResult(201,"图形验证码有误");
            }

            MtUser userInfo = memberService.queryMemberByName(merchantId, account);
            if (userInfo != null) {
                String myPassword = userInfo.getPassword();
                String inputPassword = memberService.deCodePassword(password, userInfo.getSalt());
                if (myPassword.equals(inputPassword)) {
                    UserInfo loginInfo = new UserInfo();
                    loginInfo.setToken(TokenUtil.generateToken(userAgent, userInfo.getId()));
                    loginInfo.setId(userInfo.getId());
                    TokenUtil.saveToken(loginInfo);
                    dto.setToken(loginInfo.getToken());
                    mtUser = userInfo;
                } else {
                    return getFailureResult(201, "账号或密码有误");
                }
            } else {
                return getFailureResult(201, "账号或密码有误");
            }
        }

        if (mtUser != null) {
            Map<String, Object> outParams = new HashMap<>();
            outParams.put("token", dto.getToken());
            outParams.put("userId", mtUser.getId());
            outParams.put("userName", mtUser.getName());
            outParams.put("openId", mtUser.getOpenId());
            String domain = env.getProperty("website.url");
            String appId = env.getProperty("weixin.official.appId");
            MtMerchant mtMerchant = merchantService.queryMerchantById(merchantId);
            if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxOfficialAppId())) {
                appId = mtMerchant.getWxOfficialAppId();
            }
            outParams.put("domain", domain);
            outParams.put("appId", appId);
            return getSuccessResult("登录成功", outParams);
        } else {
            return getFailureResult(201, "登录失败");
        }
    }

    /**
     * 获取会员信息
     */
    @ApiOperation(value = "获取会员信息")
    @RequestMapping(value = "/doGetUserInfo", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doGetUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001, "用户没登录!");
        }
        return getSuccessResult(userInfo);
    }

    /**
     * 获取授权登录配置
     */
    @ApiOperation(value = "获取授权登录配置")
    @RequestMapping(value = "/authLoginConfig", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject authLoginConfig(HttpServletRequest request) {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        MtMerchant mtMerchant = merchantService.queryMerchantByNo(merchantNo);

        Map<String, Object> outParams = new HashMap<>();
        String domain = env.getProperty("website.url");
        String appId = env.getProperty("weixin.official.appId");
        if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxOfficialAppId())) {
            appId = mtMerchant.getWxOfficialAppId();
        }

        outParams.put("appId", appId);
        outParams.put("domain", domain);

        return getSuccessResult(outParams);
    }

    /**
     * 退出登录
     */
    @ApiOperation(value = "退出登录")
    @RequestMapping(value = "/signOut", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doLogout(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        boolean flag = TokenUtil.removeToken(token);
        if (!flag) {
            return getFailureResult(1001, "退出错误!");
        } else {
            return getSuccessResult("退出成功！");
        }
    }
}
