package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fuint.common.dto.MessageResDto;
import com.fuint.common.service.SendSmsService;
import com.fuint.common.service.SmsTemplateService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtSmsSendedLogMapper;
import com.fuint.repository.model.MtSmsSendedLog;
import com.fuint.repository.model.MtSmsTemplate;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 发送手机短信服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class SendSmsServiceImpl implements SendSmsService {

     private static final Logger logger = LoggerFactory.getLogger(SendSmsServiceImpl.class);

     @Autowired
     private Environment env;

     @Resource
     private MtSmsSendedLogMapper mtSmsSendedLogMapper;

     @Autowired
     private SmsTemplateService smsTemplateService;

    @Override
    public Map<Boolean,List<String>> sendSms(String templateUname, List<String> phones, Map<String, String> contentParams) throws Exception {
        logger.info("使用短信平台发送短信.....");
        Integer mode = Integer.parseInt(env.getProperty("aliyun.sms.mode"));
        if (mode != 1) {
            throw new BusinessCheckException("未开启短信发送开关，请联系管理员！");
        }

        if (templateUname != null && !CollectionUtils.isEmpty(phones)) {
            Map<Boolean, List<String>> result = new HashMap<>();
            try {
                if (mode != null && mode.intValue() == 1) {
                    // 手机号以","分隔拼接
                    String mobilePhones = phones.stream().collect(Collectors.joining(","));
                    MessageResDto res = this.sendMessage(mobilePhones, templateUname, contentParams);
                    result.put(res.getResult(), phones);
                } else {
                    result.put(Boolean.TRUE,phones);
                    logger.info("模拟短信平台发送短信.....");
                }
            } catch (Exception e) {
                result.put(Boolean.FALSE,phones);
                logger.error("推送至短信平台出错...参数[smscontent={},phones={}]", templateUname, phones);
                logger.error(e.getMessage(),e);
            }
            return result;
        } else {
            throw new BusinessCheckException("手机号码和短信内容不能为空，请确认！");
        }
    }

    /**
     * 发送短信
     *
     * @param phoneNo         短信发送手机号，多个手机号以英文半角逗号隔开，最多支持200个手机号
     * @param templateUname   短信模板英文名称
     * @return
     */
    public MessageResDto sendMessage(String phoneNo, String templateUname, Map<String, String> contentParams) {
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

        MtSmsTemplate templateInfo = new MtSmsTemplate();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("uname", templateUname);
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
            this.smsSendedLogRecord(phoneNo, smsContent);
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
     * @param phoneNo   短信发送手机号
     * @param message   短信内容
     * @return
     */
    public void smsSendedLogRecord(String phoneNo, String message) {
        MtSmsSendedLog mtSmsSendedLog = new MtSmsSendedLog();
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
