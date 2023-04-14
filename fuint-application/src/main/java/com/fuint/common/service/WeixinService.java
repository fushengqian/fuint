package com.fuint.common.service;

import com.alibaba.fastjson.JSONObject;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtOrder;
import com.fuint.repository.model.MtUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Date;

/**
 * 微信相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface WeixinService {

    String getAccessToken(boolean useCache);

    ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException;

    Map<String,String> processResXml(HttpServletRequest request);

    void processRespXml(HttpServletResponse response, boolean flag);

    JSONObject getWxProfile(String code);

    JSONObject getWxOpenId(String code);

    String getPhoneNumber(String encryptedData, String session_key, String iv);

    Boolean sendSubscribeMessage(Integer userId, String toUserOpenId, String key, String page, Map<String,Object> params, Date sendTime) throws BusinessCheckException;

    Boolean doSendSubscribeMessage(String reqDataJsonStr);

    String queryPaidOrder(Integer storeId, String transactionId, String orderSn);

}