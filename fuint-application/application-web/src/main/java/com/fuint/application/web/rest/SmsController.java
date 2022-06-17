package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtVerifyCode;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.service.verifycode.VerifyCodeService;
import com.fuint.application.util.BizCodeGenerator;
import com.fuint.captcha.service.CaptchaService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;
import com.fuint.application.util.PhoneFormatCheckUtils;
import java.util.*;

/**
 * 短信类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/sms")
public class SmsController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    /**
     * 验证码服务接口
     */
    @Autowired
    private VerifyCodeService verifyCodeService;

    /**
     * 短信发送接口
     */
    @Autowired
    private SendSmsInterface sendSmsService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private Environment env;

    /**
     * 发送验证码短信
     */
    @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject sendVerifyCode(HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception {
        String captchaCode = param.get("captchaCode") == null ? "" : param.get("captchaCode").toString();
        if (StringUtils.isEmpty(captchaCode)) {
            return getFailureResult(1002,"图形验证码不能为空");
        }
        HttpSession session = request.getSession();
        boolean captchaVerify = captchaService.checkCode(captchaCode, session);
        if (!captchaVerify) {
            return getFailureResult(1002,"图形验证码有误");
        }

        // 验证码时间间隔
        String second = env.getProperty("SMS.PERIOD");
        if (null != second && second.length() > 0) {
            Integer.parseInt(second);
        }

        String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
        if (StringUtils.isEmpty(mobile)) {
            return getFailureResult(1002,"手机号码不能为空");
        } else {
            if (!PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
                return getFailureResult(1002,"手机号码格式不正确");
            }
        }

        // 插入验证码表
        String verifyCode= BizCodeGenerator.getVerifyCode();
        MtVerifyCode mtVerifyCode = verifyCodeService.addVerifyCode(mobile, verifyCode,60);
        if (null == mtVerifyCode) {
            return getFailureResult(1002,"验证码发送失败");
        } else if(mtVerifyCode.getValidflag().equals("1") && mtVerifyCode.getId() == null){
            return getFailureResult(1002,"验证码发送间隔太短,请稍后再试！");
        }

        // 发送短信
        Map<Boolean,List<String>> result;
        List<String> mobileList = new ArrayList<>();
        mobileList.add(mobile);

        // 短信模板
        Map<String, String> params = new HashMap<>();
        params.put("code", verifyCode);
        result = sendSmsService.sendSms("login-code", mobileList, params);

        return getSuccessResult(result);
    }
}
