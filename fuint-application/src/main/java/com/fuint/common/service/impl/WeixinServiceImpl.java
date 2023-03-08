package com.fuint.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.fuint.common.bean.WxPayBean;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.OrderUserDto;
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
import com.ijpay.wxpay.model.UnifiedOrderModel;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

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
    private UserCouponService userCouponService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private PointService pointService;

    @Autowired
    private UserGradeService userGradeService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BalanceService balanceService;

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
        String wxAppId = env.getProperty("weixin.miniProgram.appId");
        String wxAppSecret = env.getProperty("weixin.miniProgram.appSecret");
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
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip) throws BusinessCheckException {
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
        if (userInfo.getId() == 163 || userInfo.getId() == 707) {
            reqData.put("total_fee", "1");// 1分钱
        } else {
            reqData.put("total_fee", payAmount.toString());
        }
        reqData.put("spbill_create_ip", ip);

        // JSAPI支付
        if (orderInfo.getPayType().equals(PayTypeEnum.JSAPI.getKey())) {
            reqData.put("trade_type", PayTypeEnum.JSAPI.getKey());
            reqData.put("openid", userInfo.getOpenId() == null ? "" : userInfo.getOpenId());
        }

        if (StringUtil.isNotEmpty(authCode)) {
            reqData.put("auth_code", authCode);
        }

        // 更新支付金额
        BigDecimal payAmount1 = new BigDecimal(payAmount).divide(new BigDecimal("100"), BigDecimal.ROUND_CEILING);
        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderInfo.getId());
        reqDto.setPayAmount(payAmount1);
        reqDto.setPayType(orderInfo.getPayType());
        orderService.updateOrder(reqDto);

        Map<String, String> respData = unifiedOrder(orderInfo.getStoreId(), reqData, ip);
        if (respData == null) {
            logger.error("微信支付接口调用异常......");
            return new ResponseObject(3000, "微信支付接口调用异常", null);
        }

        // 2.记录支付接口请求/响应参数
        Map<String, String> outParmas;

        // 3.更新预支付订单号
        if (respData.get("return_code").equals("SUCCESS")) {
            if (respData.get("result_code").equals("FAIL")) {
                return new ResponseObject(3000, respData.get("err_code_des"), null);
            }
            String prepayId = respData.get("prepay_id");
            getApiConfig(orderInfo.getStoreId());
            WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();
            outParmas = WxPayKit.miniAppPrepayIdCreateSign(wxPayApiConfig.getAppId(), prepayId, wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
            String jsonStr = JSON.toJSONString(outParmas);
            logger.info("小程序支付的参数:" + jsonStr);
        } else {
            logger.error("微信支付接口返回状态失败......" + respData.toString() + "...reason");
            return new ResponseObject(3000, "微信支付接口返回状态失败", null);
        }

        ResponseObject responseObject = new ResponseObject(200, "微信支付接口返回状态失败", outParmas);
        logger.info("WXService createPrepayOrder outParams {}", responseObject.toString());

        return responseObject;
    }

    /**
     * 支付回调
     * @return
     * */
    @Override
    @Transactional
    public boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException {
        // 更新订单状态为已支付
        boolean isPay = orderService.setOrderPayed(orderInfo.getId());
        if (!isPay) {
            return false;
        }

        // 储值卡订单
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            Map<String, Object> param = new HashMap<>();
            param.put("couponId", orderInfo.getCouponId());
            param.put("userId", orderInfo.getUserId());
            param.put("param", orderInfo.getParam());
            param.put("orderId", orderInfo.getId());
            userCouponService.preStore(param);
        }

        // 充值订单
        if (orderInfo.getType().equals(OrderTypeEnum.RECHARGE.getKey())) {
            // 余额支付
            MtBalance mtBalance = new MtBalance();
            OrderUserDto userDto = orderInfo.getUserInfo();

            if (userDto.getMobile() != null && StringUtil.isNotEmpty(userDto.getMobile())) {
                mtBalance.setMobile(userDto.getMobile());
            }

            mtBalance.setOrderSn(orderInfo.getOrderSn());
            mtBalance.setUserId(orderInfo.getUserId());

            String param = orderInfo.getParam();
            if (StringUtil.isNotEmpty(param)) {
                String params[] = param.split("_");
                if (params.length == 2) {
                    BigDecimal amount = new BigDecimal(params[0]).add(new BigDecimal(params[1]));
                    mtBalance.setAmount(amount);
                    balanceService.addBalance(mtBalance);
                }
            }
        }

        // 处理消费返积分，查询返1积分所需消费金额
        MtSetting setting = settingService.querySettingByName("pointNeedConsume");
        if (setting != null) {
            String needPayAmount = setting.getValue();
            Integer needPayAmountInt = Math.round(Integer.parseInt(needPayAmount));

            Double pointNum = 0d;
            if (orderInfo.getPayAmount().compareTo(new BigDecimal(needPayAmountInt)) > 0) {
                BigDecimal point = orderInfo.getPayAmount().divide(new BigDecimal(needPayAmountInt), BigDecimal.ROUND_CEILING);
                pointNum = Math.ceil(point.doubleValue());
            }

            logger.info("WXService paymentCallback Point orderSn = {} , pointNum ={}", orderInfo.getOrderSn(), pointNum);

            if (pointNum > 0) {
                MtUser userInfo = memberService.queryMemberById(orderInfo.getUserId());
                MtUserGrade userGrade = userGradeService.queryUserGradeById(Integer.parseInt(userInfo.getGradeId()));

                // 是否会员积分加倍
                if (userGrade.getSpeedPoint() > 1) {
                    pointNum = pointNum * userGrade.getSpeedPoint();
                }

                MtPoint reqPointDto = new MtPoint();
                reqPointDto.setAmount(pointNum.intValue());
                reqPointDto.setUserId(orderInfo.getUserId());
                reqPointDto.setOrderSn(orderInfo.getOrderSn());
                reqPointDto.setDescription("支付￥"+orderInfo.getPayAmount()+"返"+pointNum+"积分");
                reqPointDto.setOperator("系统");
                pointService.addPoint(reqPointDto);
            }
        }

        logger.info("WXService paymentCallback Success orderSn {}", orderInfo.getOrderSn());
        return true;
    }

    public Map<String, String> processResXml(HttpServletRequest request) {
        try {
            String xmlMsg = HttpKit.readData(request);
            logger.info("支付通知=" + xmlMsg);
            Map<String, String> result = WxPayKit.xmlToMap(xmlMsg);
            String returnCode = result.get("return_code");
            getApiConfig(0);
            if (WxPayKit.verifyNotify(result, WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(), SignType.HMACSHA256)) {
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
        String wxAppId = env.getProperty("weixin.miniProgram.appId");
        String wxAppSecret = env.getProperty("weixin.miniProgram.appSecret");
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
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
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
    public boolean sendSubscribeMessage(Integer userId, String toUserOpenId, String key, String page, Map<String,Object> params, Date sendTime) throws BusinessCheckException {
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
    public boolean doSendSubscribeMessage(String reqDataJsonStr) {
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

    private Map<String, String> unifiedOrder(Integer storeId, Map<String, String> reqData, String ip) {
        try {
            logger.info("调用微信支付下单接口入参{}", JsonUtil.toJSONString(reqData));
            // 小程序支付
            getApiConfig(storeId);
            WxPayApiConfig wxPayApiConfig = WxPayApiConfigKit.getWxPayApiConfig();

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
                    .notify_url(wxPayBean.getDomain(null))
                    .trade_type(reqData.get("trade_type"))
                    .openid(reqData.get("openid"))
                    .build()
                    .createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);
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

    private WxPayApiConfig getApiConfig(Integer storeId) {
        WxPayApiConfig apiConfig;
        MtStore mtStore = new MtStore();
        try {
            apiConfig = WxPayApiConfigKit.getApiConfig(wxPayBean.getAppId(mtStore));
        } catch (Exception e) {
            apiConfig = WxPayApiConfig.builder()
                    .appId(wxPayBean.getAppId(mtStore))
                    .mchId(wxPayBean.getMchId(mtStore))
                    .partnerKey(wxPayBean.getPartnerKey(mtStore))
                    .certPath(wxPayBean.getCertPath(mtStore))
                    .domain(wxPayBean.getDomain(mtStore))
                    .build();
        }
        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(apiConfig);
        return apiConfig;
    }
}
