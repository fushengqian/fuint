package com.fuint.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.oss.OSS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuint.common.Constants;
import com.fuint.common.bean.H5SceneInfo;
import com.fuint.common.bean.WxPayBean;
import com.fuint.common.bean.shoppingOrders.*;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.dto.WxCardDto;
import com.fuint.common.enums.*;
import com.fuint.common.http.HttpRESTDataClient;
import com.fuint.common.service.*;
import com.fuint.common.util.*;
import com.fuint.common.vo.printer.OrderStatusType;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtUploadShippingLogMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.QRCodeUtil;
import com.fuint.utils.StringUtil;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.model.MicroPayModel;
import com.ijpay.wxpay.model.OrderQueryModel;
import com.ijpay.wxpay.model.RefundModel;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import lombok.AllArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.env.Environment;
import weixin.popular.util.JsonUtil;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
@AllArgsConstructor(onConstructor_= {@Lazy})
public class WeixinServiceImpl implements WeixinService {

    private static final Logger logger = LoggerFactory.getLogger(WeixinServiceImpl.class);

    private MtUploadShippingLogMapper uploadShippingLogMapper;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 系统消息服务接口
     * */
    private MessageService messageService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 支付服务接口
     * */
    private PaymentService paymentService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    private Environment env;

    private WxPayBean wxPayBean;

    private static final String CALL_BACK_URL = "/clientApi/pay/weixinCallback";

    private static final String REFUND_NOTIFY_URL = "/clientApi/pay/weixinRefundNotify";

    private static final String FUINT_ACCESS_TOKEN_PRE = "FUINT_ACCESS_TOKEN";

    /**
     * 获取微信accessToken
     *
     * @param merchantId 商户ID
     * @param isMinApp 是否小程序
     * @param useCache 是否读取缓存
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public String getAccessToken(Integer merchantId, boolean isMinApp, boolean useCache) throws BusinessCheckException {
        String platForm = isMinApp == true ? "minApp" : "mp";
        String wxAppId = env.getProperty("weixin.official.appId");
        String wxAppSecret = env.getProperty("weixin.official.appSecret");
        if (isMinApp) {
            wxAppId = env.getProperty("wxpay.appId");
            wxAppSecret = env.getProperty("wxpay.appSecret");
        }
        String tokenKey = FUINT_ACCESS_TOKEN_PRE + platForm;
        if (merchantId != null && merchantId > 0) {
            MtMerchant mtMerchant = merchantService.queryMerchantById(merchantId);
            if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxAppId()) && StringUtil.isNotEmpty(mtMerchant.getWxAppSecret())) {
                wxAppId = mtMerchant.getWxAppId();
                wxAppSecret = mtMerchant.getWxAppSecret();
                tokenKey = tokenKey + merchantId;
            }
        }

        String wxTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        String url = String.format(wxTokenUrl, wxAppId, wxAppSecret);
        String token = "";

        if (useCache) {
            token = RedisUtil.get(tokenKey);
        }

        if (token == null || StringUtil.isEmpty(token)) {
            try {
                String response = HttpRESTDataClient.requestGet(url);
                JSONObject json = (JSONObject) JSONObject.parse(response);
                if (!json.containsKey("errcode")) {
                    RedisUtil.set(tokenKey, json.get("access_token"), 7200);
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
     *
     * @param userInfo 会员信息
     * @param orderInfo 订单信息
     * @param payAmount 支付金额
     * @param authCode 付款码
     * @param giveAmount 赠送金额
     * @param ip 支付IP
     * @param platform 支付平台
     * @param isWechat 是否微信客户端
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform, String isWechat) throws BusinessCheckException {
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
        reqDto.setOrderSn(orderInfo.getOrderSn());
        orderService.updateOrder(reqDto);

        Map<String, String> respData;
        if (reqData.get("auth_code") != null && StringUtil.isNotEmpty(reqData.get("auth_code"))) {
            respData = microPay(orderInfo.getStoreId(), reqData, ip, platform);
        } else {
            if (platform.equals(PlatformTypeEnum.H5.getCode()) && isWechat.equals(YesOrNoEnum.NO.getKey())) {
                respData = wapPay(orderInfo.getStoreId(), reqData, ip, platform);
            } else {
                respData = jsapiPay(orderInfo.getStoreId(), reqData, ip, platform);
            }
        }
        logger.info("微信支付接口调用返回:{}", JsonUtil.toJSONString(respData));

        if (respData == null || respData.get("return_code").equals("FAIL")) {
            logger.error("微信支付接口调用异常......");
            return new ResponseObject(3000, "微信支付接口调用异常", null);
        }

        // 2.更新预支付订单号
        if (respData.get("result_code").equals("SUCCESS")) {
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
            return new ResponseObject(3000, "微信支付失败:" + (respData.get("err_code_des") != null ? respData.get("err_code_des") : "未知错误"), null);
        }

        ResponseObject responseObject = new ResponseObject(200, "微信支付接口返回成功", respData);
        logger.info("WXService createPrepayOrder outParams {}", responseObject.toString());

        return responseObject;
    }

    /**
     * 处理支付回调
     *
     * @param request 请求参数
     * @return
     * */
    public Map<String, String> processResXml(HttpServletRequest request) {
        try {
            String xmlMsg = HttpKit.readData(request);
            Map<String, String> result = WxPayKit.xmlToMap(xmlMsg);
            String returnCode = result.get("return_code");
            String orderSn = result.get("out_trade_no");
            logger.info("支付通知，xml = {}, orderSn = {}", xmlMsg, orderSn);

            Integer storeId = 0;
            String platform = PlatformTypeEnum.MP_WEIXIN.getCode();
            if (StringUtil.isNotEmpty(orderSn)) {
                MtOrder mtOrder = orderService.getOrderInfoByOrderSn(orderSn);
                if (mtOrder != null) {
                    storeId = mtOrder.getStoreId();
                    platform = mtOrder.getPlatform();
                }
            }

            getApiConfig(storeId, platform);
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

    /**
     * 处理回调xml
     *
     * @param response 响应参数
     * @param flag 标记
     * @return
     * */
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
        } finally {
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
     *
     * @param merchantId 商户ID
     * @param code 微信返回编码
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public JSONObject getWxProfile(Integer merchantId, String code) throws BusinessCheckException {
        String wxAppId = env.getProperty("wxpay.appId");
        String wxAppSecret = env.getProperty("wxpay.appSecret");

        if (merchantId != null && merchantId > 0) {
            MtMerchant mtMerchant = merchantService.queryMerchantById(merchantId);
            if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxAppId()) && StringUtil.isNotEmpty(mtMerchant.getWxAppSecret())) {
                wxAppId = mtMerchant.getWxAppId();
                wxAppSecret = mtMerchant.getWxAppSecret();
            }
        }

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
     *
     * @param merchantId 商户ID
     * @param code 微信返回编码
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public JSONObject getWxOpenId(Integer merchantId, String code) throws BusinessCheckException {
        String wxAppId = env.getProperty("weixin.official.appId");
        String wxAppSecret = env.getProperty("weixin.official.appSecret");

        if (merchantId != null && merchantId > 0) {
            MtMerchant mtMerchant = merchantService.queryMerchantById(merchantId);
            if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxOfficialAppId()) && StringUtil.isNotEmpty(mtMerchant.getWxOfficialAppSecret())) {
                wxAppId = mtMerchant.getWxOfficialAppId();
                wxAppSecret = mtMerchant.getWxOfficialAppSecret();
            }
        }

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
     *
     * @param encryptedData 微信返回加密字符串
     * @param sessionKey session键值
     * @param iv 微信IV
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
     * 发送小程序订阅消息
     *
     * @param merchantId 商户ID
     * @param userId 会员ID
     * @param toUserOpenId 接受者openId
     * @param key 消息key
     * @param page 跳转页面
     * @param params 发送参数
     * @param sendTime 发送时间
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public Boolean sendSubscribeMessage(Integer merchantId, Integer userId, String toUserOpenId, String key, String page, Map<String,Object> params, Date sendTime) throws BusinessCheckException {
        if (StringUtil.isEmpty(toUserOpenId) || StringUtil.isEmpty(key) || userId < 1) {
            return false;
        }

        MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.SUB_MESSAGE.getKey(), key);
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
        mtMessage.setMerchantId(merchantId);
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

    /**
     * 发送订阅消息
     *
     * @param merchantId 商户ID
     * @param reqDataJsonStr 请求参数
     * @return
     * */
    @Override
    public Boolean doSendSubscribeMessage(Integer merchantId, String reqDataJsonStr) {
        try {
            String token = getAccessToken(merchantId, true,true);
            if (StringUtil.isEmpty(token)) {
                return false;
            }
            String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + token;
            String response = HttpRESTDataClient.requestPost(url, "application/json; charset=utf-8", reqDataJsonStr);
            logger.info("WeixinService sendSubscribeMessage response={}", response);
            JSONObject json = (JSONObject) JSONObject.parse(response);
            if (json.get("errcode").toString().equals("40001")) {
                getAccessToken(merchantId, true,true);
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
     *
     * @param storeId 店铺ID
     * @param transactionId 支付流水ID
     * @param orderSn 订单号
     * @return
     * */
    @Override
    public Map<String, String> queryPaidOrder(Integer storeId, String transactionId, String orderSn) {
        try {
            MtOrder mtOrder = orderService.getOrderInfoByOrderSn(orderSn);
            String platform = PlatformTypeEnum.MP_WEIXIN.getCode();
            if (mtOrder != null) {
                platform = mtOrder.getPlatform();
            }
            getApiConfig(storeId, platform);
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
            Map<String, String> result = WxPayKit.xmlToMap(query);
            logger.info("查询结果: {}", result);
            if (result.get("result_code").equals("FAIL")) {
                result.put("trade_state", "FAIL");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发起售后
     *
     * @param storeId 店铺ID
     * @param orderSn 订单号
     * @param totalAmount 支付总金额
     * @param refundAmount 退款金额
     * @param platform 支付平台
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public Boolean doRefund(Integer storeId, String orderSn, BigDecimal totalAmount, BigDecimal refundAmount, String platform) throws BusinessCheckException {
        try {
            logger.info("WeixinService.doRefund orderSn = {}, totalFee = {}, refundFee = {}", orderSn, totalAmount, refundAmount);
            if (StringUtil.isEmpty(orderSn)) {
                throw new BusinessCheckException("退款订单号不能为空...");
            }

            BigDecimal totalFee = totalAmount.multiply(new BigDecimal("100"));
            BigDecimal refundFee = refundAmount.multiply(new BigDecimal("100"));
            Integer totalFeeInt = totalFee.intValue();
            Integer refundFeeInt = refundFee.intValue();
            if (refundFee.compareTo(totalFee) > 0) {
                throw new BusinessCheckException("退款金额不能大于总金额");
            }

            // 支付配置
            getApiConfig(storeId, platform);
            WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
            Map<String, String> params = RefundModel.builder()
                    .appid(wxPayApiConfig.getAppId())
                    .mch_id(wxPayApiConfig.getMchId())
                    .nonce_str(WxPayKit.generateStr())
                    .transaction_id("")
                    .out_trade_no(orderSn)
                    .out_refund_no(orderSn)
                    .total_fee(totalFeeInt.toString())
                    .refund_fee(refundFeeInt.toString())
                    .notify_url(wxPayApiConfig.getDomain() + REFUND_NOTIFY_URL)
                    .build()
                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5);
            logger.info("WeixinService doRefund params: {}", params);
            String refundStr = WxPayApi.orderRefundByProtocol(false, params, wxPayApiConfig.getCertPath(), wxPayApiConfig.getMchId(), "");
            logger.info("WeixinService doRefund return: {}", refundStr);
            Map<String, String> result = WxPayKit.xmlToMap(refundStr);
            String returnCode = result.get("return_code");
            String returnMsg = result.get("return_msg");
            if (!WxPayKit.codeIsOk(returnCode)) {
                logger.error(returnMsg);
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new BusinessCheckException("WeixinService.doRefund 微信退款失败：" + e.getMessage());
        }
    }

    /***
     * 生成二维码
     *
     * @param merchantId 商户ID
     * @param type 类型
     * @param id 数据ID
     * @param page 页面
     * @param width 宽度
     * @return
     * */
    @Override
    public String createQrCode(Integer merchantId, String type, Integer id, String page, Integer width) throws BusinessCheckException {
        try {
            String accessToken = getAccessToken(merchantId, true,true);
            if (StringUtil.isEmpty(accessToken)) {
                throw new BusinessCheckException("生成二维码出错，请检查小程序配置");
            }

            String url = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token=" + accessToken;
            String reqDataJsonStr = "";

            Map<String, Object> reqData = new HashMap<>();
            reqData.put("access_token", accessToken);
            reqData.put("path", page);
            reqData.put("width", width);
            reqDataJsonStr = JsonUtil.toJSONString(reqData);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("path", page);
            jsonParam.put("width", width);

            InputStream inputStream = HttpRESTDataClient.doWXPost(url, jsonParam);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n;
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            byte[] bytes = output.toByteArray();
            String resStr = output.toString();
            logger.info("WechatService createStoreQrCode reqData：{},resStr:{}", reqDataJsonStr, resStr);
            try {
                JSONObject res = JSON.parseObject(resStr);
                String errCode = res.get("errcode").toString();
                if (errCode.equals("40001")) {
                    getAccessToken(merchantId, true, false);
                }
            } catch (Exception e) {
                String pathRoot = env.getProperty("images.root");
                String baseImage = env.getProperty("images.path");

                String filePath = "Qr" + type + id + ".png";
                String path = pathRoot + baseImage + filePath;
                QRCodeUtil.saveQrCodeToLocal(bytes, path);

                // 上传阿里云oss
                String mode = env.getProperty("aliyun.oss.mode");
                if (mode.equals("1")) { // 检查是否开启上传
                    String endpoint = env.getProperty("aliyun.oss.endpoint");
                    String accessKeyId = env.getProperty("aliyun.oss.accessKeyId");
                    String accessKeySecret = env.getProperty("aliyun.oss.accessKeySecret");
                    String bucketName = env.getProperty("aliyun.oss.bucketName");
                    String folder = env.getProperty("aliyun.oss.folder");
                    OSS ossClient = AliyunOssUtil.getOSSClient(accessKeyId, accessKeySecret, endpoint);
                    File ossFile = new File(path);
                    return AliyunOssUtil.upload(ossClient, ossFile, bucketName, folder);
                } else {
                    return baseImage + filePath;
                }
            }
        } catch (Exception e) {
            logger.error("生成店铺二维码出错啦：{}", e.getMessage());
            throw new BusinessCheckException("生成二维码出错，请检查小程序配置.");
        }

        throw new BusinessCheckException("生成二维码出错，请稍后再试.");
    }

    /**
     * 开通微信卡券
     *
     * @param merchantId 商户ID
     * @param wxCardId 微信会员卡ID
     * @return
     * */
    @Override
    public String createWxCard(Integer merchantId, String wxCardId) throws BusinessCheckException {
        String cardId = "";
        try {
            MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.USER.getKey(), UserSettingEnum.WX_MEMBER_CARD.getKey());
            if (mtSetting == null) {
                return cardId;
            }
            WxCardDto wxCardDto = JsonUtil.parseObject(mtSetting.getValue(), WxCardDto.class);

            String accessToken = getAccessToken(merchantId, false,true);
            String createUrl = "https://api.weixin.qq.com/card/create?access_token=" + accessToken;
            String updateUrl = "https://api.weixin.qq.com/card/update?access_token=" + accessToken;

            Map<String, Object> params = new HashMap<>();
            Map<String, Object> card = new HashMap<>();
            if (StringUtil.isEmpty(wxCardId)) {
                card.put("card_type", "MEMBER_CARD");
            }
            Map<String, Object> memberCard = new HashMap<>();
            String baseImage = settingService.getUploadBasePath();
            if (StringUtil.isNotEmpty(wxCardDto.getBackgroundUrl())) {
                // memberCard.put("background_pic_url", baseImage + wxCardDto.getBackgroundUrl());
            }

            // baseInfo
            Map<String, Object> baseInfo = new HashMap<>();
            if (StringUtil.isNotEmpty(wxCardDto.getLogoUrl())) {
                baseInfo.put("logo_url", baseImage + wxCardDto.getLogoUrl());
            }
            if (StringUtil.isEmpty(wxCardId)) {
                baseInfo.put("brand_name", wxCardDto.getBrandName());
            }
            baseInfo.put("code_type", "CODE_TYPE_TEXT");
            baseInfo.put("title", wxCardDto.getTitle());
            baseInfo.put("color", wxCardDto.getColor());
            baseInfo.put("notice", wxCardDto.getNotice());
            if (StringUtil.isNotEmpty(wxCardDto.getServicePhone())) {
                baseInfo.put("service_phone", wxCardDto.getServicePhone());
            }
            baseInfo.put("description", wxCardDto.getDescription());
            Map<String, Object> dateInfo = new HashMap<>();
            dateInfo.put("type", "DATE_TYPE_PERMANENT");
            if (StringUtil.isEmpty(wxCardId)) {
                baseInfo.put("date_info", dateInfo);
            }
            Map<String, Object> sku = new HashMap<>();
            sku.put("quantity", Constants.ALL_ROWS);
            if (StringUtil.isEmpty(wxCardId)) {
                baseInfo.put("sku", sku);
                baseInfo.put("get_limit", 1);
            }
            if (StringUtil.isEmpty(wxCardId)) {
                baseInfo.put("use_custom_code", false);
                baseInfo.put("bind_openid", false);
            }
            baseInfo.put("can_give_friend", false);
            if (StringUtil.isEmpty(wxCardId)) {
                baseInfo.put("location_id_list", null);
            }
            if (StringUtil.isNotEmpty(wxCardDto.getCustomUrlName())) {
                baseInfo.put("custom_url_name", wxCardDto.getCustomUrlName());
            }
            if (StringUtil.isNotEmpty(wxCardDto.getCustomUrl())) {
                baseInfo.put("custom_url", wxCardDto.getCustomUrl());
            }
            if (StringUtil.isNotEmpty(wxCardDto.getCustomUrlSubTitle())) {
                baseInfo.put("custom_url_sub_title", wxCardDto.getCustomUrlSubTitle());
            }
            baseInfo.put("need_push_on_view", true);
            memberCard.put("base_info", baseInfo);

            // 特权说明
            if (StringUtil.isNotEmpty(wxCardDto.getPrerogative())) {
                memberCard.put("prerogative", wxCardDto.getPrerogative());
            }
            // 自动激活
            memberCard.put("auto_activate", true);
            memberCard.put("supply_bonus", wxCardDto.getSupplyBonus());
            if (StringUtil.isNotEmpty(wxCardDto.getBonusUrl())) {
                memberCard.put("bonus_url", wxCardDto.getBonusUrl());
            }
            if (StringUtil.isEmpty(wxCardId)) {
                memberCard.put("supply_balance", wxCardDto.getSupplyBalance());
            }
            if (StringUtil.isNotEmpty(wxCardDto.getBalanceUrl())) {
                memberCard.put("balance_url", wxCardDto.getBalanceUrl());
            }
            card.put("member_card", memberCard);
            if (StringUtil.isEmpty(wxCardId)) {
                params.put("card", card);
            } else {
                card.put("card_id", wxCardId);
                params = card;
            }

            ObjectMapper mapper = new ObjectMapper();
            String reqDataJson = mapper.writeValueAsString(params);
            String url = createUrl;
            if (StringUtil.isNotEmpty(wxCardId)) {
                url = updateUrl;
            }
            logger.info("开通微信卡券接口url：{}，请求参数：{}", url, reqDataJson);
            String response = HttpRESTDataClient.requestPost(url, "application/json; charset=utf-8", reqDataJson);
            logger.info("开通微信卡券接口返回：{}", response);
            JSONObject data = (JSONObject) JSONObject.parse(response);
            if (data.get("errcode").toString().equals("0")) {
                if (StringUtil.isEmpty(wxCardId)) {
                    cardId = data.get("card_id").toString();
                } else {
                    cardId = wxCardId;
                }
            } else {
                // token失效，刷新token
                if (data.get("errcode").toString().equals("40014")) {
                    getAccessToken(merchantId, false,false);
                }
                logger.error("开通微信卡券出错啦{}", data.get("errmsg").toString());
                throw new BusinessCheckException("开通微信卡券出错啦：" + data.get("errmsg").toString());
            }
        } catch (Exception e) {
            logger.error("开通微信卡券出错啦：{}", e.getMessage());
            throw new BusinessCheckException("开通微信卡券出错啦：" + e.getMessage());
        }

        return cardId;
    }

    /**
     * 创建微信卡券领取的二维码
     *
     * @param merchantId 商户ID
     * @param cardId 微信卡券ID
     * @param code 会员卡编码
     * @return
     * */
    @Override
    public String createCardQrCode(Integer merchantId, String cardId, String code) {
        try {
            String accessToken = getAccessToken(merchantId, false, true);
            String url = "https://api.weixin.qq.com/card/qrcode/create?access_token="+accessToken;

            Map<String, Object> param = new HashMap<>();
            Map<String, Object> actionInfo = new HashMap<>();
            Map<String, Object> card = new HashMap<>();
            card.put("card_id", cardId);
            card.put("code", code);
            card.put("is_unique_code", false);
            card.put("outer_str", "12b");
            actionInfo.put("card", card);
            param.put("action_name", "QR_CARD");
            param.put("action_info", actionInfo);

            String reqDataJsonStr = JsonUtil.toJSONString(param);
            String response = HttpRESTDataClient.requestPostBody(url, reqDataJsonStr);
            logger.info("微信卡券createCardQrCode接口返回：{}", response);
            JSONObject data = (JSONObject) JSONObject.parse(response);
            String qrCode = "";
            if (data.get("errcode").toString().equals("0")) {
                String content = data.get("url").toString();
                try {
                    // 生成并输出二维码
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    com.fuint.common.util.QRCodeUtil.createQrCode(out, content, 800, 800, "png", "");
                    // 对数据进行Base64编码
                    qrCode = new String(Base64Util.baseEncode(out.toByteArray()), "UTF-8");
                    return "data:image/jpg;base64," + qrCode;
                } catch (Exception e) {
                    logger.error("生成并输出二维码出错：{}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("创建微信卡券领取二维码出错：{}", e.getMessage());
            return "";
        }
        return "";
    }

    /**
     * 是否已领取卡券
     *
     * @param merchantId 商户ID
     * @param cardId 微信卡券ID
     * @param openId openId
     * @return
     * */
    @Override
    public Boolean isOpenCard(Integer merchantId, String cardId, String openId) {
        try {
            String accessToken = getAccessToken(merchantId, false,true);
            String url = "https://api.weixin.qq.com/card/user/getcardlist?access_token="+accessToken;

            Map<String, Object> param = new HashMap<>();
            param.put("openid", openId);
            param.put("card_id", cardId);

            String reqDataJsonStr = JsonUtil.toJSONString(param);
            String response = HttpRESTDataClient.requestPostBody(url, reqDataJsonStr);
            logger.info("微信卡券getCardList接口返回：{}", response);
            JSONObject data = (JSONObject) JSONObject.parse(response);
            if (data.get("errcode").toString().equals("0")) {
                Object cards = data.get("card_list");
                logger.info("微信卡券getCardList接口card_list：{}", cards.toString());
                if (cards != null && cards.toString().length() > 6) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            logger.error("微信卡券getCardList接口出错：{}", e.getMessage());
            return true;
        }
        return true;
    }

    /**
     * 生成小程序链接
     *
     * @param merchantId 商户ID
     * @param path 页面路径
     * @return
     * */
    @Override
    public String createMiniAppLink(Integer merchantId, String path) {
        String link = "";
        try {
            String accessToken = getAccessToken(merchantId, true,true);
            if (StringUtil.isEmpty(accessToken)) {
                return "";
            }
            String url = "https://api.weixin.qq.com/wxa/genwxashortlink?access_token=" + accessToken +"&";

            Map<String, Object> param = new HashMap<>();
            param.put("page_url", path);

            String reqDataJsonStr = JsonUtil.toJSONString(param);
            String response = HttpRESTDataClient.requestPostBody(url, reqDataJsonStr);
            logger.info("微信生成链接接口返回：{}", response);
            JSONObject data = (JSONObject) JSONObject.parse(response);

            if (data.get("errcode").toString().equals("0")) {
                Object linkObject = data.get("link");
                if (linkObject != null && StringUtil.isNotEmpty(linkObject.toString())) {
                    link = linkObject.toString();
                }
            }
        } catch (Exception e) {
            logger.error("微信生成链接接口出错：{}", e.getMessage());
            return "";
        }

        return link;
    }

    /**
     * 上传小程序发货信息
     *
     * @param orderSn 订单号
     * @return
     */
    @Override
    public void uploadShippingInfo(String orderSn) throws BusinessCheckException {
        UserOrderDto orderInfo = orderService.getOrderByOrderSn(orderSn);
        if (orderInfo == null) {
            return;
        }
        if (orderInfo.getExpressInfo() == null || StringUtil.isEmpty(orderInfo.getExpressInfo().getExpressNo())) {
            throw new BusinessCheckException("上传发货信息失败，物流信息不能为空！");
        }
        if (orderInfo.getUserInfo() == null || StringUtil.isEmpty(orderInfo.getUserInfo().getOpenId())) {
            throw new BusinessCheckException("上传发货信息失败，会员的openId不能为空！");
        }
        // 是否是微信小程序订单 && 微信支付
        if (orderInfo != null && orderInfo.getPlatform().equals(PlatformTypeEnum.MP_WEIXIN.getCode()) || orderInfo.getPayType().equals(PayTypeEnum.JSAPI.name())) {
            String url = "https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token=" + getAccessToken(orderInfo.getMerchantId(), true, true);

            // 获取微信支付配置
            getApiConfig(orderInfo.getStoreId(), orderInfo.getPlatform());
            WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();

            // 组织上传参数
            ShippingInfo shippingInfo = new ShippingInfo();

            // 1、订单参数
            OrderKeyBean orderKeyBean = new OrderKeyBean();
            orderKeyBean.setOrderNumberType(1);
            orderKeyBean.setMchId(wxPayApiConfig.getMchId());
            orderKeyBean.setOutTradeNo(orderSn);
            shippingInfo.setOrderKey(orderKeyBean);

            // 2、物流模式，发货方式枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提
            shippingInfo.setLogisticsType(1);

            // 3、发货模式，发货模式枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货） 示例值: UNIFIED_DELIVERY
            shippingInfo.setDeliveryMode(1);

            // 4、物流信息列表，发货物流单列表，支持统一发货（单个物流单）和分拆发货（多个物流单）两种模式
            List<ShippingListBean> shippingList = new ArrayList<>();
            ShippingListBean shippingListBean = new ShippingListBean();
            shippingListBean.setTrackingNo(orderInfo.getExpressInfo().getExpressNo());
            shippingListBean.setExpressCompany(orderInfo.getExpressInfo().getExpressCode());
            ContactBean contact = new ContactBean();
            contact.setConsignorContact(orderInfo.getStoreInfo().getPhone());
            contact.setReceiverContact(orderInfo.getAddress().getMobile());
            shippingListBean.setContact(contact);

            shippingList.add(shippingListBean);
            shippingInfo.setShippingList(shippingList);

            // 5、支付者信息
            PayerBean payerBean = new PayerBean();
            payerBean.setOpenid(orderInfo.getUserInfo().getOpenId());
            shippingInfo.setPayer(payerBean);

            try {
                String reqJson = JsonUtil.toJSONString(shippingInfo);
                String response = HttpRESTDataClient.requestPostBody(url, reqJson);
                logger.info("微信上传发货信息接口参数：{}，返回：{}", reqJson, response);
                JSONObject data = (JSONObject) JSONObject.parse(response);
                MtUploadShippingLog mtUploadShippingLog = new MtUploadShippingLog();
                mtUploadShippingLog.setMerchantId(orderInfo.getMerchantId());
                mtUploadShippingLog.setStoreId(orderInfo.getStoreId());
                mtUploadShippingLog.setOrderId(orderInfo.getId());
                mtUploadShippingLog.setOrderSn(orderSn);
                mtUploadShippingLog.setMobile(orderInfo.getAddress().getMobile());
                Date time = new Date();
                mtUploadShippingLog.setCreateTime(time);
                mtUploadShippingLog.setUpdateTime(time);
                if (data.get("errcode").toString().equals("0")) {
                    logger.info("微信上传发货信息接口成功，订单号：", orderSn);
                    mtUploadShippingLog.setStatus(OrderStatusType.Completed.getVal());
                } else {
                    mtUploadShippingLog.setStatus(OrderStatusType.Failed.getVal());
                }
                uploadShippingLogMapper.insert(mtUploadShippingLog);
            } catch (Exception e) {
                logger.error("微信上传发货信息接口失败：", e.getMessage());
            }
        }
    }

    /**
     * 刷卡支付
     *
     * @param storeId 店铺ID
     * @param reqData 请求参数
     * @param ip 支付IP
     * @param platform 支付平台
     * @return
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
                Map<String, String> payResult = null;
                String errCode = respMap.get("err_code");
                if (StringUtil.isNotEmpty(errCode)) {
                    // 用户支付中，需要输入密码
                    if (errCode.equals("USERPAYING")) {
                        // 等待10秒后查询订单
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        payResult = queryPaidOrder(storeId, respMap.get("transaction_id"), orderSn);
                    }
                }
                if (payResult == null || !payResult.get("trade_state").equals("SUCCESS")) {
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
     *
     * @param storeId 店铺ID
     * @param reqData 请求参数
     * @param ip 支付IP
     * @param platform 支付平台
     * @return
     * */
    private Map<String, String> jsapiPay(Integer storeId, Map<String, String> reqData, String ip, String platform) {
        try {
            logger.info("调用微信支付下单接口入参：{}，请求平台：{}", JsonUtil.toJSONString(reqData), platform);
            // 获取支付配置
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
                    .notify_url(wxPayApiConfig.getDomain() + CALL_BACK_URL)
                    .trade_type(reqData.get("trade_type"))
                    .openid(reqData.get("openid"))
                    .build()
                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.MD5);
            String xmlResult = WxPayApi.pushOrder(false, params);

            logger.info("调用微信支付回调地址：{}", wxPayApiConfig.getDomain() + CALL_BACK_URL);
            logger.info("调用微信支付下单接口返回xml：{}", xmlResult);
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

            logger.info("调用微信支付下单接口返回数据：{}", JsonUtil.toJSONString(result));
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * h5支付
     *
     * @param storeId 店铺ID
     * @param reqData 请求参数
     * @param ip 支付IP
     * @param platform 支付平台
     * @return
     * */
    private Map<String, String> wapPay(Integer storeId, Map<String, String> reqData, String ip, String platform) {
        try {
            logger.info("调用微信h5支付下单接口入参{}，请求平台：{}", JsonUtil.toJSONString(reqData), platform);
            // 支付配置
            getApiConfig(storeId, platform);
            WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
            H5SceneInfo sceneInfo = new H5SceneInfo();
            H5SceneInfo.H5 h5Info = new H5SceneInfo.H5();
            h5Info.setType("Wap");
            // 支付域名必须在商户平台->"产品中心"->"开发配置"中添加
            h5Info.setWap_url(wxPayApiConfig.getDomain());
            h5Info.setWap_name("WEB");
            sceneInfo.setH5Info(h5Info);
            Map<String, String> params = UnifiedOrderModel
                    .builder()
                    .appid(wxPayApiConfig.getAppId())
                    .mch_id(wxPayApiConfig.getMchId())
                    .nonce_str(WxPayKit.generateStr())
                    .body(reqData.get("body"))
                    .attach(reqData.get("body"))
                    .out_trade_no(WxPayKit.generateStr())
                    .total_fee(reqData.get("total_fee"))
                    .spbill_create_ip(ip)
                    .notify_url(wxPayApiConfig.getDomain() + CALL_BACK_URL)
                    .trade_type(TradeType.MWEB.getTradeType())
                    .scene_info(JSON.toJSONString(sceneInfo))
                    .build()
                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);

            String xmlResult = WxPayApi.pushOrder(false, params);
            logger.info("调用微信h5支付接口返回xml：{}", xmlResult);

            Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

            String return_code = result.get("return_code");
            String return_msg = result.get("return_msg");
            if (!WxPayKit.codeIsOk(return_code)) {
                throw new RuntimeException(return_msg);
            }
            String result_code = result.get("result_code");
            if (!WxPayKit.codeIsOk(result_code)) {
                throw new RuntimeException(return_msg);
            }
            result.put("backUrl", env.getProperty("website.url"));
            logger.info("调用微信h5支付接口返回数据：{}", JsonUtil.toJSONString(result));
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取支付配置
     *
     * @param storeId 店铺ID
     * @param platform 支付平台
     * @throws BusinessCheckException
     * @return
     * */
    private WxPayApiConfig getApiConfig(Integer storeId, String platform) throws BusinessCheckException {
        WxPayApiConfig apiConfig;

        String mchId = wxPayBean.getMchId();
        String apiV2 = wxPayBean.getApiV2();
        String certPath = wxPayBean.getCertPath();
        String appId = wxPayBean.getAppId();

        MtStore mtStore = storeService.queryStoreById(storeId);
        logger.info("微信支付店铺信息：{}", JsonUtil.toJSONString(mtStore));
        if (mtStore != null && StringUtil.isNotEmpty(mtStore.getWxApiV2()) && StringUtil.isNotEmpty(mtStore.getWxMchId())) {
            mchId = mtStore.getWxMchId();
            apiV2 = mtStore.getWxApiV2();
            String basePath = env.getProperty("images.root");
            certPath = basePath + mtStore.getWxCertPath();
            MtMerchant mtMerchant = merchantService.queryMerchantById(mtStore.getMerchantId());
            if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxAppId())) {
                appId = mtMerchant.getWxAppId();
            }
        }

        apiConfig = WxPayApiConfig.builder()
                   .appId(appId)
                   .mchId(mchId)
                   .partnerKey(apiV2)
                   .certPath(certPath)
                   .domain(wxPayBean.getDomain())
                   .build();

        // 微信内h5公众号支付或PC收银
        if (platform.equals(PlatformTypeEnum.H5.getCode()) || platform.equals(PlatformTypeEnum.PC.getCode())) {
            String wxAppId = env.getProperty("weixin.official.appId");
            String wxAppSecret = env.getProperty("weixin.official.appSecret");

            if (mtStore != null) {
                MtMerchant mtMerchant = merchantService.queryMerchantById(mtStore.getMerchantId());
                if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxOfficialAppId()) && StringUtil.isNotEmpty(mtMerchant.getWxOfficialAppSecret())) {
                    wxAppId = mtMerchant.getWxOfficialAppId();
                    wxAppSecret = mtMerchant.getWxOfficialAppSecret();
                }
            }

            if (StringUtil.isNotEmpty(wxAppId) && StringUtil.isNotEmpty(wxAppSecret)) {
                apiConfig.setAppId(wxAppId);
                apiConfig.setApiKey(wxAppSecret);
            }
        }

        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(apiConfig);
        logger.info("微信支付参数：{}", JsonUtil.toJSONString(apiConfig));

        return apiConfig;
    }
}
