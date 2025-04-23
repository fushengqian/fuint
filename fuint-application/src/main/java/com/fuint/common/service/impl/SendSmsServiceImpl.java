package com.fuint.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fuint.common.dto.MessageResDto;
import com.fuint.common.enums.SettingTypeEnum;
import com.fuint.common.enums.SmsSettingEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SendSmsService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.SmsTemplateService;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtSmsSendedLogMapper;
import com.fuint.repository.model.MtSetting;
import com.fuint.repository.model.MtSmsSendedLog;
import com.fuint.repository.model.MtSmsTemplate;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 发送手机短信服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor
public class SendSmsServiceImpl implements SendSmsService {

    private static final Logger logger = LoggerFactory.getLogger(SendSmsServiceImpl.class);

    /**
     * 系统环境变量
     * */
    private Environment env;

    private MtSmsSendedLogMapper mtSmsSendedLogMapper;

    /**
     * 短信模板服务接口
     * */
    private SmsTemplateService smsTemplateService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 发送短信
     *
     * @param merchantId 商户ID
     * @param templateUname 模板名
     * @param phones 发送手机号
     * @param contentParams 发送参数
     * @return
     * */
    @Override
    public Map<Boolean,List<String>> sendSms(Integer merchantId, String templateUname, List<String> phones, Map<String, String> contentParams) throws BusinessCheckException {
        logger.info("使用短信平台发送短信.....");
        Map<Boolean, List<String>> result = new HashMap<>();
        Integer mode = Integer.parseInt(env.getProperty("aliyun.sms.mode"));
        MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.SMS_CONFIG.getKey(), SmsSettingEnum.IS_CLOSE.getKey());
        if (mode.intValue() == 1 && mtSetting != null && StringUtil.isNotEmpty(mtSetting.getValue())) {
            mode = Integer.parseInt(mtSetting.getValue());
            logger.info("商户短信设置 mtSetting = {}", JSON.toJSONString(mtSetting));
        }
        if (mode.intValue() != 1) {
            logger.info("短信平台未开启 mode = {}", mode);
            return result;
        }
        if (templateUname != null && !CollectionUtils.isEmpty(phones)) {
            try {
                if (mode != null && mode.intValue() == 1) {
                    // 手机号以","分隔拼接
                    String mobilePhones = phones.stream().collect(Collectors.joining(","));
                    MessageResDto res = sendMessage(merchantId, mobilePhones, templateUname, contentParams);
                    result.put(res.getResult(), phones);
                } else {
                    result.put(Boolean.TRUE,phones);
                    logger.info("模拟短信平台发送短信.....");
                }
            } catch (Exception e) {
                result.put(Boolean.FALSE,phones);
                logger.error("推送至短信平台出错，参数[templateUname={}，phones={}]", templateUname, phones);
                logger.error(e.getMessage(),e);
            }
        } else {
            logger.error("手机号码和短信内容不能为空，请确认！");
        }
        return result;
    }

    /**
     * 发送短信
     *
     * @param phoneNo         短信发送手机号，多个手机号以英文半角逗号隔开，最多支持200个手机号
     * @param templateUname   短信模板英文名称
     * @return
     */
    public MessageResDto sendMessage(Integer merchantId, String phoneNo, String templateUname, Map<String, String> contentParams) throws BusinessCheckException {
        MessageResDto resInfo = new MessageResDto();
        logger.info("sendMessage inParams:phoneNo={}, message={}", phoneNo, templateUname);
        if (StringUtil.isBlank(phoneNo) || phoneNo.split(",").length > 200) {
            logger.error("手机号列表不符合要求");
            resInfo.setResult(Boolean.FALSE);
            return resInfo;
        }

        String accessKeyId = env.getProperty("aliyun.sms.accessKeyId");
        String secret = env.getProperty("aliyun.sms.accessKeySecret");
        String signName = env.getProperty("aliyun.sms.signName");

        List<MtSetting> settings = settingService.getSettingList(merchantId, SettingTypeEnum.SMS_CONFIG.getKey());
        if (settings != null && settings.size() > 0) {
            logger.info("商户短信设置 mtSetting = {}", JSON.toJSONString(settings.get(0)));
            String accessKeyId1 = "";
            String secret1 = "";
            String signName1 = "";
            for (MtSetting mtSetting : settings) {
                 if (mtSetting.getName().equals(SmsSettingEnum.ACCESS_KEY_ID.getKey()) && StringUtil.isNotEmpty(mtSetting.getValue())) {
                     accessKeyId1 = mtSetting.getValue();
                 }
                 if (mtSetting.getName().equals(SmsSettingEnum.ACCESS_KEY_SECRET.getKey()) && StringUtil.isNotEmpty(mtSetting.getValue())) {
                     secret1 = mtSetting.getValue();
                 }
                 if (mtSetting.getName().equals(SmsSettingEnum.SIGN_NAME.getKey()) && StringUtil.isNotEmpty(mtSetting.getValue())) {
                     signName1 = mtSetting.getValue();
                 }
            }
            if (StringUtil.isNotEmpty(accessKeyId1) && StringUtil.isNotEmpty(secret1) && StringUtil.isNotEmpty(signName1)) {
                accessKeyId = accessKeyId1;
                secret = secret1;
                signName = signName1;
            }
        }

        MtSmsTemplate templateInfo = new MtSmsTemplate();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("uname", templateUname);
            params.put("merchant_id", merchantId);
            List<MtSmsTemplate> templateList = smsTemplateService.querySmsTemplateByParams(params);
            if (templateList.size() < 1) {
                throw new BusinessCheckException("该短信模板不存在！");
            }
            templateInfo = templateList.get(0);
            if (!templateInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                throw new BusinessCheckException("该短信模板未启用！");
            }
        } catch (BusinessCheckException e) {
            e.getStackTrace();
        }

        boolean flag = false;
        try {
            // 解决中文乱码
            if (!CommonUtil.isUtf8(signName) || CommonUtil.isErrCode(signName)) {
                signName = new String(signName.getBytes("ISO8859-1"), "UTF-8");
            }

            // 阿里云短信
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
            IAcsClient client = new DefaultAcsClient(profile);

            // 装配参数
            String smsContent = templateInfo.getContent();
            if (smsContent == null || StringUtil.isEmpty(smsContent)) {
                resInfo.setResult(Boolean.FALSE);
                return resInfo;
            }
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
                logger.info("sendMessage response:{}", response.toString());
                res = response.getData();
                System.out.println(response.getData());
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
            logger.info("sendMessage outParams:{}", res);
            saveSendLog(merchantId, phoneNo, smsContent);
            flag = true;
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
     * @param merchantId 商户ID
     * @param phoneNo    短信发送手机号
     * @param message    短信内容
     * @return
     */
    public void saveSendLog(Integer merchantId, String phoneNo, String message) {
        MtSmsSendedLog mtSmsSendedLog = new MtSmsSendedLog();
        mtSmsSendedLog.setMerchantId(merchantId);
        mtSmsSendedLog.setMobilePhone(phoneNo);
        mtSmsSendedLog.setContent(message);
        Date time = new Date();
        mtSmsSendedLog.setCreateTime(time);
        mtSmsSendedLog.setSendTime(time);
        mtSmsSendedLog.setUpdateTime(time);
        mtSmsSendedLogMapper.insert(mtSmsSendedLog);
    }

    /**
     * 分页查询已发短信列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtSmsSendedLog> querySmsListByPagination(PaginationRequest paginationRequest) {
        Page<MtSmsSendedLog> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtSmsSendedLog> lambdaQueryWrapper = Wrappers.lambdaQuery();
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtSmsSendedLog::getMerchantId, merchantId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtSmsSendedLog::getStoreId, storeId);
        }
        String content = paginationRequest.getSearchParams().get("content") == null ? "" : paginationRequest.getSearchParams().get("content").toString();
        if (StringUtils.isNotBlank(content)) {
            lambdaQueryWrapper.like(MtSmsSendedLog::getContent, content);
        }
        String mobile = paginationRequest.getSearchParams().get("mobile") == null ? "" : paginationRequest.getSearchParams().get("mobile").toString();
        if (StringUtils.isNotBlank(mobile)) {
            lambdaQueryWrapper.eq(MtSmsSendedLog::getMobilePhone, mobile);
        }

        lambdaQueryWrapper.orderByDesc(MtSmsSendedLog::getLogId);
        List<MtSmsSendedLog> dataList = mtSmsSendedLogMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtSmsSendedLog> paginationResponse = new PaginationResponse(pageImpl, MtSmsSendedLog.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }
}
