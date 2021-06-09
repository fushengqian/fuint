package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dao.entities.MtVerifyCode;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.sms.SendSmsInterface;
import com.fuint.application.service.verifycode.VerifyCodeService;
import com.fuint.application.util.BizCodeGenerator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fuint.application.BaseController;
import com.fuint.application.ResponseObject;

import java.util.*;
import com.fuint.application.util.PhoneFormatCheckUtils;

/**
 * 短信类controller
 * Created by zach on 2019/7/16.
 */
@RestController
@RequestMapping(value = "/rest/sms")
public class SmsController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    /**
     * 会员服务接口
     */
    @Autowired
    private MemberService memberService;

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
    private Environment env;

    /**
     * 发送验证码短信
     *
     * @param request  HttpServletRequest对象
     * @param model    SpringFramework Model对象
     */
    @RequestMapping(value = "/sendVerifyCode", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject sendVerifyCode(HttpServletRequest request, @RequestBody Map<String, Object> param, Model model) throws Exception {

        String second = env.getProperty("SMS.PERIOD");

        // 验证码时间间隔
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

        // 检验手机号是否是会员
        MtUser tempUser = memberService.queryMemberByMobile(mobile);
        if (null == tempUser) {
            return getFailureResult(1002,"该手机号码不是会员");
        }
        if (!tempUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
            return getFailureResult(1002,"该会员手机号码已经注销");
        }

        // 插入验证码表
        String verifyCode= BizCodeGenerator.getVerifyCode();
        MtVerifyCode mtVerifyCode=verifyCodeService.addVerifyCode(mobile,verifyCode,60);
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
