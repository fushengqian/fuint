package com.fuint.application.service.weixin;

import com.alibaba.fastjson.JSONObject;
import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.exception.BusinessCheckException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Date;

/**
 * 微信相关接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
public interface WeixinService {
    String getAccessToken(boolean useCache);
    ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip) throws BusinessCheckException;
    boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException;;
    Map<String,String> processResXml(HttpServletRequest request);
    void processRespXml(HttpServletResponse response, boolean flag);
    JSONObject getWxProfile(String code);
    String getPhoneNumber(String encryptedData, String session_key, String iv);
    boolean sendSubscribeMessage(Integer userId, String toUserOpenId, String key, String page, Map<String,Object> params, Date sendTime) throws BusinessCheckException;
    boolean doSendSubscribeMessage(String reqDataJsonStr);
}