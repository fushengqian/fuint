package com.fuint.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fuint.common.bean.WxPayBean;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.http.HttpRESTDataClient;
import com.fuint.common.service.*;
import com.fuint.common.util.RedisUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.model.MicroPayModel;
import com.ijpay.wxpay.model.OrderQueryModel;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.env.Environment;
import weixin.popular.util.JsonUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.*;

/**
 * 微信相关接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class WeixinServiceImpl implements WeixinService {

    private static final Logger logger = LoggerFactory.getLogger(WeixinServiceImpl.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private Environment env;

    @Autowired
    WxPayBean wxPayBean;

    /**
     * 获取微信accessToken
     * @param useCache 是否读取缓存
     * @return
     * */
    @Override
    public String getAccessToken(boolean useCache) {
        String wxAppId = env.getProperty("wxpay.appId");
        String wxAppSecret = env.getProperty("wxpay.appSecret");
        String wxTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

        String url = String.format(wxTokenUrl, wxAppId, wxAppSecret);
        String token = "";

        if (useCache) {
            token = RedisUtil.get("FUINT_ACCESS_TOKEN");
        }

        if (token == null || StringUtil.isEmpty(token)) {
            try {
                String response = HttpRESTDataClient.requestGet(url);
                JSONObject json = (JSONObject) JSONObject.parse(response);
                if (!json.containsKey("errcode")) {
                    RedisUtil.set("FUINT_ACCESS_TOKEN", json.get("access_token"), 7200);
                    token = (String) json.get("access_token");
                } else {
                    logger.error("获取微信accessToken出错：" + json.get("errmsg"));
                }
            } catch (Exception e) {
                logger.error("获取微信accessToken异常：" + e.getMessage());
            }
        }

        return token;
    }

    /**
     * 创建支付订单
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException {
        logger.info("WeixinService createPrepayOrder inParams userInfo={} payAmount={} giveAmount={} goodsInfo={}", userInfo, payAmount, giveAmount, orderInfo);

        String goodsInfo = orderInfo.getOrderSn();
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            goodsInfo = OrderTypeEnum.PRESTORE.getValue();
        }

        // 1. 调用微信接口生成预支付订单
        Map<String, String> reqData = new HashMap<>();
        reqData.put("body", goodsInfo);
        reqData.put("out_trade_no", orderInfo.getOrderSn());
        reqData.put("device_info", "");
        reqData.put("fee_type", "CNY");
        reqData.put("total_fee", payAmount.toString());
        reqData.put("spbill_create_ip", ip);

        // JSAPI支付
        if (orderInfo.getPayType().equals(PayTypeEnum.JSAPI.getKey())) {
            reqData.put("trade_type", PayTypeEnum.JSAPI.getKey());
            reqData.put("openid", userInfo.getOpenId() == null ? "" : userInfo.getOpenId());
        }

        // 刷卡支付
        if (StringUtil.isNotEmpty(authCode)) {
            reqData.put("auth_code", authCode);
        }

        // 更新支付金额
        BigDecimal payAmount1 = new BigDecimal(payAmount).divide(new BigDecimal("100"));
        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderInfo.getId());
        reqDto.setPayAmount(payAmount1);
        reqDto.setPayType(orderInfo.getPayType());
        orderService.updateOrder(reqDto);

        Map<String, String> respData;
        if (reqData.get("auth_code") != null && StringUtil.isNotEmpty(reqData.get("auth_code"))) {
            respData = microPay(orderInfo.getStoreId(), reqData, ip, platform);
        } else {
            respData = jsapiPay(orderInfo.getStoreId(), reqData, ip, platform);
        }

        if (respData == null) {
            logger.error("微信支付接口调用异常......");
            return new ResponseObject(3000, "微信支付接口调用异常", null);
        }

        // 2.更新预支付订单号
        if (respData.get("return_code").equals("SUCCESS")) {
            if (respData.get("trade_type").equals(PayTypeEnum.JSAPI.getKey())) {
                String prepayId = respData.get("prepay_id");
                getApiConfig(orderInfo.getStoreId(), platform);
                WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
                respData = WxPayKit.miniAppPrepayIdCreateSign(wxPayApiConfig.getAppId(), prepayId, wxPayApiConfig.getPartnerKey(), SignType.MD5);
                String jsonStr = JSON.toJSONString(respData);
                logger.info("小程序支付的参数:" + jsonStr);
            }
        } else {
            logger.error("微信支付接口返回状态失败......" + respData.toString() + "...reason");
            return new ResponseObject(3000, "微信支付接口返回状态失败", null);
        }

        ResponseObject responseObject = new ResponseObject(200, "微信支付接口返回成功", respData);
        logger.info("WXService createPrepayOrder outParams {}", responseObject.toString());

        return responseObject;
    }

    public Map<String, String> processResXml(HttpServletRequest request) {
        try {
            String xmlMsg = HttpKit.readData(request);
            logger.info("支付通知=" + xmlMsg);
            Map<String, String> result = WxPayKit.xmlToMap(xmlMsg);
            String returnCode = result.get("return_code");
            getApiConfig(0, PlatformTypeEnum.MP_WEIXIN.getCode());
            if (WxPayKit.verifyNotify(result, WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(), SignType.MD5)) {
                if (WxPayKit.codeIsOk(returnCode)) {
                    return result;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public void processRespXml(HttpServletResponse response, boolean flag){
        Map<String,String> respData = new HashMap<>();
        if (flag) {
            respData.put("return_code", "SUCCESS");
            respData.put("return_msg", "OK");
        }else{
            respData.put("return_code", "FAIL");
            respData.put("return_msg", "FAIL");
        }
        OutputStream outputStream = null;
        try {
            String respXml = WxPayKit.toXml(respData);
            outputStream = response.getOutputStream();
            outputStream.write(respXml.getBytes("UTF-8"));
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 获取微信个人信息
     * @return
     * */
    @Override
    public JSONObject getWxProfile(String code) {
        String wxAppId = env.getProperty("wxpay.appId");
        String wxAppSecret = env.getProperty("wxpay.appSecret");
        String wxAccessUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
        String url = String.format(wxAccessUrl, wxAppId, wxAppSecret, code);
        try {
            String response = HttpRESTDataClient.requestGet(url);
            JSONObject json = (JSONObject) JSONObject.parse(response);
            if (!json.containsKey("errcode")) {
                return json;
            } else {
                logger.error("获取微信getWxProfile出错：code = " + json.containsKey("errcode") + ",msg="+ json.get("errmsg"));
            }
        } catch (Exception e) {
            logger.error("获取微信getWxProfile异常：" + e.getMessage());
        }

        return null;
    }

    /**
     * 获取公众号openId
     * @return
     * */
    @Override
    public JSONObject getWxOpenId(String code) {
        String wxAppId = env.getProperty("weixin.official.appId");
        String wxAppSecret = env.getProperty("weixin.official.appSecret");
        String wxAccessUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String url = String.format(wxAccessUrl, wxAppId, wxAppSecret, code);
        try {
            String response = HttpRESTDataClient.requestGet(url);
            JSONObject json = (JSONObject) JSONObject.parse(response);
            if (!json.containsKey("errcode")) {
                return json;
            } else {
                logger.error("获取openId出错：code = " + json.containsKey("errcode") + ",msg="+ json.get("errmsg"));
            }
        } catch (Exception e) {
            logger.error("获取微信openId异常：" + e.getMessage());
        }

        return null;
    }

    /**
     * 获取微信绑定手机号
     * @return
     * */
    @Override
    public String getPhoneNumber(String encryptedData, String sessionKey, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.getDecoder().decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.getDecoder().decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.getDecoder().decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                JSONObject object = JSONObject.parseObject(result);
                return object.getString("phoneNumber");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 发送订阅消息
     * @return
     * */
    @Override
    public Boolean sendSubscribeMessage(Integer userId, String toUserOpenId, String key, String page, Map<String,Object> params, Date sendTime) throws BusinessCheckException {
        if (StringUtil.isEmpty(toUserOpenId) || StringUtil.isEmpty(key) || userId < 1) {
            return false;
        }

        MtSetting mtSetting = settingService.querySettingByName(key);
        if (mtSetting == null) {
            return false;
        }

        JSONObject jsonObject = null;
        String templateId = "";
        JSONArray paramArray = null;
        try {
            if (mtSetting != null && mtSetting.getValue().indexOf('}') > 0) {
                jsonObject = JSONObject.parseObject(mtSetting.getValue());
            }
            if (jsonObject != null) {
                templateId = jsonObject.get("templateId").toString();
                paramArray = (JSONArray) JSONObject.parse(jsonObject.get("params").toString());
            }
        } catch (Exception e) {
            logger.info("WeixinService sendSubscribeMessage parse setting error={}", mtSetting);
        }

        if (StringUtil.isEmpty(templateId) || paramArray.size() < 1) {
            logger.info("WeixinService sendSubscribeMessage setting error={}", mtSetting);
            return false;
        }

        JSONObject jsonData = new JSONObject();
        jsonData.put("touser", toUserOpenId); // 接收者的openid
        jsonData.put("template_id", templateId);

        if (StringUtil.isEmpty(page)) {
            page = "pages/index/index";
        }
        jsonData.put("page", page);

        // 组装参数
        JSONObject data = new JSONObject();
        for (int i = 0; i < paramArray.size(); i++) {
             JSONObject para = paramArray.getJSONObject(i);
             String value = para.get("value").toString().replaceAll("\\{", "").replaceAll(".DATA}}", "");
             String paraKey = para.get("key").toString();
             String paraValue = params.get(paraKey).toString();
             JSONObject arg = new JSONObject();
             arg.put("value", paraValue);
             data.put(value, arg);
        }
        jsonData.put("data", data);

        String reqDataJsonStr = JSON.toJSONString(jsonData);

        // 存储到消息表里，后续通过定时任务发送
        MtMessage mtMessage = new MtMessage();
        mtMessage.setUserId(userId);
        mtMessage.setType(MessageEnum.SUB_MSG.getKey());
        mtMessage.setTitle(WxMessageEnum.getValue(key));
        mtMessage.setContent(WxMessageEnum.getValue(key));
        mtMessage.setIsRead(YesOrNoEnum.NO.getKey());
        mtMessage.setIsSend(YesOrNoEnum.NO.getKey());
        mtMessage.setSendTime(sendTime);
        mtMessage.setStatus(StatusEnum.ENABLED.getKey());
        mtMessage.setParams(reqDataJsonStr);
        messageService.addMessage(mtMessage);

        return true;
    }

    @Override
    public Boolean doSendSubscribeMessage(String reqDataJsonStr) {
        try {
            String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + this.getAccessToken(true);
            String response = HttpRESTDataClient.requestPost(url, "application/json; charset=utf-8", reqDataJsonStr);
            logger.info("WeixinService sendSubscribeMessage response={}", response);
            JSONObject json = (JSONObject) JSONObject.parse(response);
            if (json.get("errcode").toString().equals("40001")) {
                this.getAccessToken(false);
                logger.error("发送订阅消息出错error1：" + json.get("errcode").toString());
                return false;
            } else if (!json.get("errcode").toString().equals("0")) {
                logger.error("发送订阅消息出错error2：" + json.get("errcode").toString());
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("发送订阅消息出错：" + e.getMessage());
        }

        return true;
    }

    /**
     * 查询支付订单
     * */
    @Override
    public String queryPaidOrder(Integer storeId, String transactionId, String orderSn) {
        try {
            getApiConfig(storeId, PlatformTypeEnum.MP_WEIXIN.getCode());
            WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
            Map<String, String> params = OrderQueryModel.builder()
                    .appid(wxPayApiConfig.getAppId())
                    .mch_id(wxPayApiConfig.getMchId())
                    .transaction_id(transactionId)
                    .out_trade_no(orderSn)
                    .nonce_str(WxPayKit.generateStr())
                    .build()
                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5);
            logger.info("请求参数：{}", WxPayKit.toXml(params));
            String query = WxPayApi.orderQuery(params);
            logger.info("查询结果: {}", query);
            return query;
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }

    /**
     * 刷卡支付
     * */
    private Map<String, String> microPay(Integer storeId, Map<String, String> reqData, String ip, String platform) {
        try {
            String orderSn = reqData.get("out_trade_no");

            logger.info("调用微信刷卡支付下单接口入参{}", JsonUtil.toJSONString(reqData));
            logger.info("请求平台：{}, 订单号：{}", platform, orderSn);

            // 支付配置
            getApiConfig(storeId, platform);
            WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
            Map<String, String> params = MicroPayModel.builder()
                    .appid(wxPayApiConfig.getAppId())
                    .mch_id(wxPayApiConfig.getMchId())
                    .nonce_str(WxPayKit.generateStr())
                    .body(reqData.get("body"))
                    .attach(reqData.get("body"))
                    .out_trade_no(orderSn)
                    .total_fee(reqData.get("total_fee"))
                    .spbill_create_ip(ip)
                    .auth_code(reqData.get("auth_code"))
                    .build()
                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5);
            String xmlResult = WxPayApi.microPay(false, params);

            // 同步返回结果
            logger.info("xmlResult:" + xmlResult);
            Map<String, String> respMap = WxPayKit.xmlToMap(xmlResult);
            String returnCode = respMap.get("return_code");
            String returnMsg = respMap.get("return_msg");
            if (!WxPayKit.codeIsOk(returnCode)) {
                // 通讯失败
                String payResult = "";
                String errCode = respMap.get("err_code");
                if (StringUtil.isNotEmpty(errCode)) {
                    // 用户支付中，需要输入密码
                    if (errCode.equals("USERPAYING")) {
                        // 等待5秒后调用
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        payResult = queryPaidOrder(storeId, respMap.get("transaction_id"), orderSn);
                    }
                }
                if (StringUtil.isEmpty(payResult) || payResult.equals("FAIL")) {
                    logger.info("提交刷卡支付失败>>" + xmlResult);
                    return respMap;
                }
            }

            String resultCode = respMap.get("result_code");
            if (!WxPayKit.codeIsOk(resultCode)) {
                logger.info("支付失败>>" + xmlResult);
                logger.error(returnMsg);
                return respMap;
            }

            // 支付成功
            logger.info("刷卡支付返回>>" + respMap.toString());

            if (StringUtil.isNotEmpty(orderSn)) {
                UserOrderDto orderInfo = orderService.getOrderByOrderSn(orderSn);
                if (orderInfo != null) {
                    if (!orderInfo.getStatus().equals(OrderStatusEnum.DELETED.getKey())) {
                        paymentService.paymentCallback(orderInfo);
                    }
                }
            }

            return respMap;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 小程序、公众号支付
     * */
    private Map<String, String> jsapiPay(Integer storeId, Map<String, String> reqData, String ip, String platform) {
        try {
            logger.info("调用微信支付下单接口入参{}", JsonUtil.toJSONString(reqData));
            logger.info("请求平台：{}", platform);
            // 支付配置
            getApiConfig(storeId, platform);
            WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();

            Map<String, String> params = UnifiedOrderModel
                    .builder()
                    .appid(wxPayApiConfig.getAppId())
                    .mch_id(wxPayApiConfig.getMchId())
                    .nonce_str(WxPayKit.generateStr())
                    .body(reqData.get("body"))
                    .attach(reqData.get("body"))
                    .out_trade_no(reqData.get("out_trade_no"))
                    .total_fee(reqData.get("total_fee"))
                    .spbill_create_ip(ip)
                    .notify_url(wxPayApiConfig.getDomain())
                    .trade_type(reqData.get("trade_type"))
                    .openid(reqData.get("openid"))
                    .build()
                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5);
            String xmlResult = WxPayApi.pushOrder(false, params);

            logger.info(xmlResult);
            Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

            String returnCode = result.get("return_code");
            String returnMsg = result.get("return_msg");
            if (!WxPayKit.codeIsOk(returnCode)) {
                logger.error(returnMsg);
            }
            String resultCode = result.get("result_code");
            if (!WxPayKit.codeIsOk(resultCode)) {
                logger.error(returnMsg);
            }

            logger.info("调用微信支付下单接口返回{}", JsonUtil.toJSONString(result));
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取支付配置
     * @param storeId
     * @param platform
     * */
    private WxPayApiConfig getApiConfig(Integer storeId, String platform) throws BusinessCheckException {
        WxPayApiConfig apiConfig;
        MtStore mtStore = storeService.queryStoreById(storeId);
        String mchId = wxPayBean.getMchId();
        String apiV2 = wxPayBean.getApiV2();
        if (mtStore != null && StringUtil.isNotEmpty(mtStore.getWxApiV2()) && StringUtil.isNotEmpty(mtStore.getWxMchId())) {
            mchId = mtStore.getWxMchId();
            apiV2 = mtStore.getWxApiV2();
        }

        apiConfig = WxPayApiConfig.builder()
                   .appId(wxPayBean.getAppId())
                   .mchId(mchId)
                   .partnerKey(apiV2)
                   .certPath(wxPayBean.getCertPath())
                   .domain(wxPayBean.getDomain())
                   .build();

        // 微信内h5公众号支付
        if (platform.equals(PlatformTypeEnum.H5.getCode())) {
            String wxAppId = env.getProperty("weixin.official.appId");
            String wxAppSecret = env.getProperty("weixin.official.appSecret");
            if (StringUtil.isNotEmpty(wxAppId) && StringUtil.isNotEmpty(wxAppSecret)) {
                apiConfig.setAppId(wxAppId);
                apiConfig.setApiKey(wxAppSecret);
            }
        }

        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(apiConfig);
        return apiConfig;
    }
}
