package com.fuint.coupon.service.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fuint.coupon.dao.entities.MtSmsTemplate;
import com.fuint.coupon.service.smstemplate.SmsTemplateService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.util.StringUtil;
import com.fuint.coupon.dao.entities.MtSmsSendedLog;
import com.fuint.coupon.dao.repositories.MtSmsSendedLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 调用短信平台Service
 * Created by zach on 201909199
 */
@Service
public class SmsPlatformService {

    private static final Logger logger = LoggerFactory.getLogger(SmsPlatformService.class);
    @Autowired
    private Environment env;

    @Autowired
    private MtSmsSendedLogRepository mtSmsSendedLogRepository;

    @Autowired
    private SmsTemplateService smsTemplateService;

     public static void main(String[] args) {
         SmsPlatformService sp=new SmsPlatformService();
         String phoneNo="18976679980";
         String template="login-code";
         Map<String, String> contentParams = new HashMap<>();
         MessageResInfo resInfo = sp.sendMessage(phoneNo, template, contentParams);
         System.out.println(resInfo);
     }

    /**
     * 发送短信
     *
     * @param phoneNo   短信发送手机号，多个手机号以英文半角逗号隔开，最多支持200个手机号
     * @param templateUname   短信模板英文名称
     * @return
     */
    public MessageResInfo sendMessage(String phoneNo, String templateUname, Map<String, String> contentParams) {
        MessageResInfo resInfo = new MessageResInfo();
        logger.info("sendMessage inParams:phoneNo={}, message={}", phoneNo, templateUname);
        if (StringUtil.isBlank(phoneNo) || phoneNo.split(",").length > 200) {
            logger.error("手机号列表不符合要求");
            resInfo.setResult(Boolean.FALSE);
            return resInfo;
        }

        String accessKeyId = env.getProperty("ALIYUN.SMS.ACCESSKRYID");
        String secret = env.getProperty("ALIYUN.SMS.SECRET");
        String signName = env.getProperty("ALIYUN.SMS.SIGNNAME");

        MtSmsTemplate templateInfo = new MtSmsTemplate();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("EQ_uname", templateUname);
            List<MtSmsTemplate> templateList = smsTemplateService.querySmsTemplateByParams(params);
            if (templateList.size() < 1) {
                throw new BusinessCheckException("该短信模板不存在！");
            }
            templateInfo = templateList.get(0);
        } catch (BusinessCheckException e) {
            e.getStackTrace();
        }

        boolean flag = false;
        try {
            // idea下中文乱码
            signName = new String(signName.getBytes("ISO8859-1"), "UTF-8");

            // 阿里云短信
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
            IAcsClient client = new DefaultAcsClient(profile);

            // 装配参数
            String smsContent = templateInfo.getContent();
            String paramJson = "";
            if (contentParams.size() > 0) {
                for (Map.Entry<String, String> entry : contentParams.entrySet()){
                     String key = entry.getKey();
                     String value = entry.getValue();
                     smsContent = smsContent.replace("{"+key+"}", value);
                }
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    paramJson = mapper.writeValueAsString(contentParams);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendSms");
            request.putQueryParameter("RegionId", "cn-hangzhou");
            request.putQueryParameter("PhoneNumbers", phoneNo);
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", templateInfo.getCode());
            request.putQueryParameter("TemplateParam", paramJson);

            String res = "";
            try {
                CommonResponse response = client.getCommonResponse(request);
                res = response.getData();
                System.out.println(response.getData());
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }

            logger.info("sendMessage outParams:{}", res);

            //log record 0919 add
            this.smsSendedLogRecord(phoneNo, smsContent);

            JSONObject resultJson = JSON.parseObject(res);
            String result = resultJson.getJSONObject("MsgResponse").getJSONObject("ResponseInfo").getString("Result");
            logger.info("result is {}", result);
            flag = "1".equals(result);
            String data = resultJson.getJSONObject("MsgResponse").getString("Data");
            if (StringUtil.isNotBlank(data)) {
                String[] datas = data.split("\\n0,");
                String[] phoneArray = phoneNo.split(",");
                String[] sendIds = new String[phoneArray.length];
                for (int i = 0; i < sendIds.length; i++) {
                    sendIds[i] = datas[i + 1];

                }
                resInfo.setSendIds(sendIds);
            }
        } catch (Exception e) {
            flag = false;
            logger.error(e.getMessage(), e);
        } finally {
            resInfo.setResult(flag);
        }
        return resInfo;
    }

    /**
     * 发送短信日志记录
     *
     * @param phoneNo   短信发送手机号
     * @param message   短信内容
     * @return
     */
    public void smsSendedLogRecord(String phoneNo, String message) {
        MtSmsSendedLog mtSmsSendedLog = new MtSmsSendedLog();
        mtSmsSendedLog.setMobilePhone(phoneNo);
        mtSmsSendedLog.setContent(message);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt=sdf.format(new Date());
            Date addtime = sdf.parse(dt);
            mtSmsSendedLog.setCreateTime(addtime);
            mtSmsSendedLog.setSendTime(addtime);
            mtSmsSendedLog.setUpdateTime(addtime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常" + e.getMessage());
        }
        mtSmsSendedLogRepository.save(mtSmsSendedLog);
    }
}
