package com.fuint.application.service.weixin;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.MtUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 微信业务接口
 * Created by zach on 2021/5/8.
 * Updated by zach on 2021/5/8.
 */
public interface WeixinService {
    ResponseObject createPrepayOrder(MtUser userInfo, String memberId, String goodsInfo, Integer payAmount, Integer giveAmount);
    boolean paymentCallback(MtUser userPayment);
    Map<String,String> processResXml(HttpServletRequest request);
    void processRespXml(HttpServletResponse response, boolean flag);
}