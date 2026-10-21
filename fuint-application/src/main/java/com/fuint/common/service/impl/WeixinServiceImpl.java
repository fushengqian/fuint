package com.fuint.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuint.common.Constants;
import com.fuint.common.bean.H5SceneInfo;
import com.fuint.common.bean.WxPayBean;
import com.fuint.common.bean.shoppingOrders.*;
import com.fuint.common.dto.member.WxCardDto;
import com.fuint.common.dto.order.OrderDto;
import com.fuint.common.dto.order.UserOrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.http.HttpRESTDataClient;
import com.fuint.common.service.*;
import com.fuint.common.util.AliyunOssUtil;
import com.fuint.common.util.Base64Util;
import com.fuint.common.util.RedisUtil;
import com.fuint.common.vo.printer.OrderStatusType;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtGoodsMapper;
import com.fuint.repository.mapper.MtOrderGoodsMapper;
import com.fuint.repository.mapper.MtUploadShippingLogMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.QRCodeUtil;
import com.fuint.utils.StringUtil;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.enums.AuthTypeEnum;
import com.ijpay.core.enums.RequestMethodEnum;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.enums.TradeType;
import com.fuint.common.util.CommonUtil;
import com.ijpay.core.kit.PayKit;
import com.ijpay.core.kit.RsaKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.enums.WxDomainEnum;
import com.ijpay.wxpay.enums.v3.BasePayApiEnum;
import com.ijpay.wxpay.model.MicroPayModel;
import com.ijpay.wxpay.model.OrderQueryModel;
import com.ijpay.wxpay.model.RefundModel;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weixin.popular.util.JsonUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.AlgorithmParameters;
import java.security.PrivateKey;
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

    /**
     * 微信支付 APIv3 配置，按线程隔离，由 getApiConfig 装配
     * */
    private static final ThreadLocal<WxPayV3Config> V3_CONFIG_HOLDER = new ThreadLocal<>();

    /**
     * 微信支付 APIv3 配置
     *
     * 包含商户私钥、商户API证书序列号、APIv3密钥、微信支付公钥
     * */
    @Data
    @Builder
    public static class WxPayV3Config {
        /** 应用编号 */
        private String appId;
        /** 商户号 */
        private String mchId;
        /** 商户API证书序列号 */
        private String serialNo;
        /** APIv3密钥，用于解密回调resource */
        private String apiV3;
        /** 商户API私钥证书文件路径 */
        private String privateKeyPath;
        /** 微信支付公钥证书文件路径，用于验证回调签名与加密敏感信息 */
        private String payPublicKeyPath;
        /** 回调域名 */
        private String domain;
    }

    private MtUploadShippingLogMapper uploadShippingLogMapper;

    private MtOrderGoodsMapper mtOrderGoodsMapper;

    private MtGoodsMapper mtGoodsMapper;

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
     * @return
     * */
    @Override
    public String getAccessToken(Integer merchantId, boolean isMinApp, boolean useCache) {
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
        reqData.put("out_trade_no", orderInfo.getPaySn());
        reqData.put("device_info", "");
        reqData.put("fee_type", "CNY");
        reqData.put("total_fee", payAmount.toString());
        reqData.put("spbill_create_ip", ip);
        if (orderInfo.getType() != null && orderInfo.getType().equals(OrderTypeEnum.GOODS.getKey())) {
            Map<String, Object> params = new HashMap<>();
            params.put("ORDER_ID", orderInfo.getId());
            params.put("STATUS", StatusEnum.ENABLED.getKey());
            List<MtOrderGoods> goodsList = mtOrderGoodsMapper.selectByMap(params);
            if (goodsList != null && goodsList.size() > 0) {
                Integer goodsId = goodsList.get(0).getGoodsId();
                if (goodsId != null && goodsId > 0) {
                    MtGoods mtGoods = mtGoodsMapper.selectById(goodsId);
                    if (mtGoods != null) {
                        reqData.put("description", mtGoods.getName());
                    }
                }
            }
        }

        // JSAPI支付（小程序、公众号内支付）
        // 注意：积分兑换订单的 payType 是 POINT，但其剩余金额（如运费）仍需走微信支付，
        // 因此除刷卡支付(MICROPAY)外统一按 JSAPI 下单，避免 trade_type 缺失导致下单失败
        if (!PayTypeEnum.MICROPAY.getKey().equals(orderInfo.getPayType())) {
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
                WxPayV3Config v3Config = getV3Config();
                PrivateKey privateKey = getV3PrivateKey(v3Config);
                // APIv3 使用商户私钥进行 RSA 签名，前端 signType 需为 RSA
                respData = WxPayKit.jsApiCreateSign(v3Config.getAppId(), prepayId, privateKey);
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
            String plainText = decryptNotify(request);
            if (StringUtil.isEmpty(plainText)) {
                return null;
            }
            logger.info("支付通知解密后明文 = {}", plainText);

            JSONObject plainJson = JSONObject.parseObject(plainText);
            String orderSn = plainJson.getString("out_trade_no");
            Map<String, String> result = new HashMap<>();
            result.put("out_trade_no", orderSn);
            result.put("transaction_id", plainJson.getString("transaction_id"));
            result.put("trade_state", plainJson.getString("trade_state"));
            result.put("return_code", "SUCCESS");
            result.put("result_code", "SUCCESS");
            logger.info("支付通知处理完成，orderSn = {}", orderSn);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解密微信支付 APIv3 回调通知
     *
     * @param request 请求体
     * @return 解密后的明文，验签或解密失败返回 null
     * */
    @Override
    public String decryptNotify(HttpServletRequest request) {
        try {
            String body = CommonUtil.readRequestBody(request);
            logger.info("微信支付回调，body = {}", body);
            String signature = request.getHeader("Wechatpay-Signature");
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");

            // resource 为密文，解密前无法得知订单号与所属门店，故逐个尝试可用配置
            for (WxPayV3Config candidate : listV3ConfigCandidates()) {
                try {
                    String plainText = WxPayKit.verifyNotify(body, signature, nonce, timestamp,
                            candidate.getApiV3(), readCertFile(candidate.getPayPublicKeyPath()));
                    if (StringUtil.isNotEmpty(plainText)) {
                        return plainText;
                    }
                } catch (Exception ignore) {
                    // 该配置与本次回调不匹配，尝试下一个
                }
            }
            logger.error("微信支付回调验签或解密失败，未找到匹配的支付配置");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 列出可用于回调验签的 APIv3 配置候选（全局配置 + 各门店配置）
     *
     * 支付回调 resource 为密文，解密前无法得知订单号与所属门店，因此需逐个尝试
     * */
    private List<WxPayV3Config> listV3ConfigCandidates() {
        List<WxPayV3Config> candidates = new ArrayList<>();
        if (StringUtil.isNotEmpty(wxPayBean.getApiV3()) && StringUtil.isNotEmpty(wxPayBean.getPayPublicKey())) {
            candidates.add(WxPayV3Config.builder()
                    .appId(wxPayBean.getAppId())
                    .mchId(wxPayBean.getMchId())
                    .serialNo(wxPayBean.getCertSerialNo())
                    .apiV3(wxPayBean.getApiV3())
                    .privateKeyPath(wxPayBean.getPrivateKeyPath())
                    .payPublicKeyPath(wxPayBean.getPayPublicKeyPath())
                    .domain(wxPayBean.getDomain())
                    .build());
        }
        try {
            String basePath = env.getProperty("images.root");
            List<MtStore> stores = storeService.queryStoresByParams(new HashMap<>());
            if (stores != null) {
                for (MtStore store : stores) {
                    if (StringUtil.isNotEmpty(store.getWxApiV3()) && StringUtil.isNotEmpty(store.getWxPayPublicKey())) {
                        candidates.add(WxPayV3Config.builder()
                                .appId(wxPayBean.getAppId())
                                .mchId(store.getWxMchId())
                                .serialNo(store.getWxCertSerialNo())
                                .apiV3(store.getWxApiV3())
                                .privateKeyPath(basePath + store.getWxPrivateKeyPath())
                                .payPublicKeyPath(basePath + store.getWxPayPublicKeyPath())
                                .domain(wxPayBean.getDomain())
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询门店支付配置失败", e);
        }
        return candidates;
    }

    /**
     * 处理回调xml
     *
     * @param response 响应参数
     * @param flag 标记
     * @return
     * */
    public void processRespXml(HttpServletResponse response, boolean flag){
        // APIv3 回调应答为 JSON，成功返回 2xx，失败返回非 2xx
        JSONObject respData = new JSONObject();
        if (flag) {
            response.setStatus(HttpServletResponse.SC_OK);
            respData.put("code", "SUCCESS");
            respData.put("message", "成功");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            respData.put("code", "FAIL");
            respData.put("message", "失败");
        }
        OutputStream outputStream = null;
        try {
            response.setContentType("application/json;charset=UTF-8");
            outputStream = response.getOutputStream();
            outputStream.write(respData.toJSONString().getBytes("UTF-8"));
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
            MtOrder mtOrder = orderService.getOrderInfoByPaySn(orderSn);
            String platform = PlatformTypeEnum.MP_WEIXIN.getCode();
            if (mtOrder != null) {
                platform = mtOrder.getPlatform();
            }
            getApiConfig(storeId, platform);
            WxPayV3Config v3Config = getV3Config();
            PrivateKey privateKey = getV3PrivateKey(v3Config);

            // APIv3 商户订单号查询
            Map<String, String> params = new HashMap<>();
            params.put("mchid", v3Config.getMchId());
            String urlSuffix = String.format(BasePayApiEnum.ORDER_QUERY_BY_OUT_TRADE_NO.getUrl(), orderSn);
            IJPayHttpResponse response = WxPayApi.v3(RequestMethodEnum.GET,
                    WxDomainEnum.CHINA.getDomain(),
                    urlSuffix,
                    v3Config.getMchId(),
                    v3Config.getSerialNo(),
                    null,
                    privateKey,
                    params);
            logger.info("查询结果: {}", response.getBody());

            Map<String, String> result = new HashMap<>();
            JSONObject respJson = JSONObject.parseObject(response.getBody());
            result.put("out_trade_no", respJson.getString("out_trade_no"));
            result.put("transaction_id", respJson.getString("transaction_id"));
            result.put("trade_state", respJson.getString("trade_state"));
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                result.put("return_code", "SUCCESS");
                result.put("result_code", "SUCCESS");
            } else {
                result.put("return_code", "FAIL");
                result.put("result_code", "FAIL");
                result.put("return_msg", respJson.getString("message"));
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
            WxPayV3Config v3Config = getV3Config();
            PrivateKey privateKey = getV3PrivateKey(v3Config);

            // APIv3 申请退款，amount 单位为分
            JSONObject amount = new JSONObject();
            amount.put("refund", refundFeeInt);
            amount.put("total", totalFeeInt);
            amount.put("currency", "CNY");
            JSONObject reqBody = new JSONObject();
            reqBody.put("out_trade_no", orderSn);
            reqBody.put("out_refund_no", orderSn);
            reqBody.put("notify_url", v3Config.getDomain() + REFUND_NOTIFY_URL);
            reqBody.put("amount", amount);

            logger.info("WeixinService doRefund params: {}", reqBody.toJSONString());
            IJPayHttpResponse response = WxPayApi.v3(RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.getDomain(),
                    BasePayApiEnum.REFUND.getUrl(),
                    v3Config.getMchId(),
                    v3Config.getSerialNo(),
                    null,
                    privateKey,
                    reqBody.toJSONString());
            logger.info("WeixinService doRefund return: {}", response.getBody());

            // APIv3 以 HTTP 状态码判定业务结果，非 2xx 表示退款失败
            if (response.getStatus() < 200 || response.getStatus() >= 300) {
                JSONObject result = JSONObject.parseObject(response.getBody());
                String message = result != null ? result.getString("message") : response.getBody();
                logger.error("微信退款业务失败，message = {}", message);
                throw new BusinessCheckException("微信退款失败：" + message);
            }
            return true;
        } catch (BusinessCheckException e) {
            throw e;
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
        // 最多重试一次（token 过期刷新后重试）
        for (int retry = 0; retry < 2; retry++) {
            try {
                String accessToken = getAccessToken(merchantId, true, retry == 0);
                if (StringUtil.isEmpty(accessToken)) {
                    throw new BusinessCheckException("生成二维码出错，请检查小程序配置");
                }

                String url = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token=" + accessToken;

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("path", page);
                jsonParam.put("width", width);

                Map<String, Object> reqData = new HashMap<>();
                reqData.put("access_token", accessToken);
                reqData.put("path", page);
                reqData.put("width", width);
                String reqDataJsonStr = JsonUtil.toJSONString(reqData);

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

                // 尝试解析为 JSON（说明微信返回了错误）
                try {
                    JSONObject res = JSON.parseObject(resStr);
                    String errCode = res.get("errcode") != null ? res.get("errcode").toString() : "";
                    String errMsg = res.get("errmsg") != null ? res.get("errmsg").toString() : "";
                    logger.error("微信生成二维码返回错误: errcode={}, errmsg={}, page={}", errCode, errMsg, page);

                    // token 过期，刷新后重试
                    if ("40001".equals(errCode) || "42001".equals(errCode)) {
                        if (retry == 0) {
                            getAccessToken(merchantId, true, false);
                            continue;
                        }
                    }
                    throw new BusinessCheckException("生成二维码出错: " + errMsg);
                } catch (BusinessCheckException e) {
                    throw e;
                } catch (Exception e) {
                    // JSON 解析失败，说明返回的是二进制图片数据，处理图片
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
            } catch (BusinessCheckException e) {
                throw e;
            } catch (Exception e) {
                logger.error("生成店铺二维码出错啦：{}", e.getMessage());
                throw new BusinessCheckException("生成二维码出错，请检查小程序配置.");
            }
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
     * @param query 页面参数
     * @return
     * */
    @Override
    public String createMiniAppLink(Integer merchantId, String path, String query) {
        String link = "";
        try {
            String accessToken = getAccessToken(merchantId, true, true);
            if (StringUtil.isEmpty(accessToken)) {
                return "";
            }
            String url = "https://api.weixin.qq.com/wxa/generate_urllink?access_token=" + accessToken;

            Map<String, Object> param = new HashMap<>();
            param.put("path", path);
            param.put("query", query);
            param.put("env_version", "release");

            String reqDataJsonStr = JsonUtil.toJSONString(param);
            String response = HttpRESTDataClient.requestPostBody(url, reqDataJsonStr);
            logger.info("微信生成链接接口返回：{}", response);
            JSONObject data = (JSONObject) JSONObject.parse(response);

            if (data.get("errcode").toString().equals("0")) {
                Object linkObject = data.get("url_link");
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
            WxPayV3Config v3Config = getV3Config();
            PrivateKey privateKey = getV3PrivateKey(v3Config);

            // APIv3 付款码支付，auth_code 属敏感信息，需使用微信支付公钥加密
            JSONObject amount = new JSONObject();
            amount.put("total", Integer.parseInt(reqData.get("total_fee")));
            amount.put("currency", "CNY");
            JSONObject payer = new JSONObject();
            payer.put("auth_code", RsaKit.encryptByPublicKeyByWx(reqData.get("auth_code"), readCertFile(v3Config.getPayPublicKeyPath())));
            JSONObject reqBody = new JSONObject();
            reqBody.put("appid", v3Config.getAppId());
            reqBody.put("mchid", v3Config.getMchId());
            reqBody.put("description", reqData.get("body"));
            reqBody.put("out_trade_no", orderSn);
            reqBody.put("amount", amount);
            reqBody.put("payer", payer);

            IJPayHttpResponse response = WxPayApi.v3(RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.getDomain(),
                    BasePayApiEnum.CODE_PAY.getUrl(),
                    v3Config.getMchId(),
                    v3Config.getSerialNo(),
                    null,
                    privateKey,
                    reqBody.toJSONString());

            // 同步返回结果
            String jsonResult = response.getBody();
            logger.info("codePayResult:" + jsonResult);
            JSONObject respJson = JSONObject.parseObject(jsonResult);
            String tradeState = respJson.getString("trade_state");
            String transactionId = respJson.getString("transaction_id");
            Map<String, String> respMap = new HashMap<>();
            respMap.put("out_trade_no", orderSn);
            respMap.put("transaction_id", transactionId);
            respMap.put("trade_state", tradeState);

            boolean userPaying = "USERPAYING".equals(tradeState)
                    || (response.getStatus() != 200 && "USERPAYING".equals(respJson.getString("code")));
            if (userPaying) {
                // 用户支付中，等待10秒后查询订单
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Map<String, String> payResult = queryPaidOrder(storeId, transactionId, orderSn);
                if (payResult == null || !"SUCCESS".equals(payResult.get("trade_state"))) {
                    logger.info("提交刷卡支付失败>>" + jsonResult);
                    respMap.put("return_code", "FAIL");
                    respMap.put("result_code", "FAIL");
                    respMap.put("return_msg", respJson.getString("message"));
                    return respMap;
                }
            } else if (response.getStatus() != 200 || !"SUCCESS".equals(tradeState)) {
                logger.info("支付失败>>" + jsonResult);
                logger.error(respJson.getString("message"));
                respMap.put("return_code", "FAIL");
                respMap.put("result_code", "FAIL");
                respMap.put("return_msg", respJson.getString("message"));
                return respMap;
            }

            respMap.put("return_code", "SUCCESS");
            respMap.put("result_code", "SUCCESS");

            // 支付成功
            logger.info("刷卡支付返回>>" + respMap.toString());

            if (StringUtil.isNotEmpty(orderSn)) {
                UserOrderDto orderInfo = orderService.getOrderByPaySn(orderSn);
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
            WxPayV3Config v3Config = getV3Config();
            PrivateKey privateKey = getV3PrivateKey(v3Config);

            // APIv3 JSAPI下单，amount.total 单位为分
            JSONObject amount = new JSONObject();
            amount.put("total", Integer.parseInt(reqData.get("total_fee")));
            amount.put("currency", "CNY");
            JSONObject payer = new JSONObject();
            payer.put("openid", reqData.get("openid"));
            JSONObject reqBody = new JSONObject();
            reqBody.put("appid", v3Config.getAppId());
            reqBody.put("mchid", v3Config.getMchId());
            reqBody.put("description", reqData.get("body"));
            reqBody.put("out_trade_no", reqData.get("out_trade_no"));
            reqBody.put("notify_url", v3Config.getDomain() + CALL_BACK_URL);
            reqBody.put("amount", amount);
            reqBody.put("payer", payer);

            logger.info("调用微信支付回调地址：{}", v3Config.getDomain() + CALL_BACK_URL);
            IJPayHttpResponse response = WxPayApi.v3(RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.getDomain(),
                    BasePayApiEnum.JS_API_PAY.getUrl(),
                    v3Config.getMchId(),
                    v3Config.getSerialNo(),
                    null,
                    privateKey,
                    reqBody.toJSONString());

            logger.info("调用微信支付下单接口返回：{}", response.getBody());
            Map<String, String> result = new HashMap<>();
            result.put("trade_type", PayTypeEnum.JSAPI.getKey());
            JSONObject respJson = JSONObject.parseObject(response.getBody());
            String prepayId = respJson.getString("prepay_id");
            if (StringUtil.isNotEmpty(prepayId)) {
                result.put("return_code", "SUCCESS");
                result.put("result_code", "SUCCESS");
                result.put("prepay_id", prepayId);
            } else {
                result.put("return_code", "FAIL");
                result.put("result_code", "FAIL");
                result.put("return_msg", respJson.getString("message"));
                result.put("err_code_des", respJson.getString("message"));
                logger.error("微信支付下单失败：{}", response.getBody());
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
            WxPayV3Config v3Config = getV3Config();
            PrivateKey privateKey = getV3PrivateKey(v3Config);

            // APIv3 H5下单，支付域名必须在商户平台->"产品中心"->"开发配置"中添加
            JSONObject amount = new JSONObject();
            amount.put("total", Integer.parseInt(reqData.get("total_fee")));
            amount.put("currency", "CNY");
            JSONObject h5Info = new JSONObject();
            h5Info.put("type", "Wap");
            h5Info.put("wap_url", v3Config.getDomain());
            h5Info.put("wap_name", "WEB");
            JSONObject sceneInfo = new JSONObject();
            sceneInfo.put("payer_client_ip", ip);
            sceneInfo.put("h5_info", h5Info);
            JSONObject reqBody = new JSONObject();
            reqBody.put("appid", v3Config.getAppId());
            reqBody.put("mchid", v3Config.getMchId());
            reqBody.put("description", reqData.get("body"));
            reqBody.put("out_trade_no", reqData.get("out_trade_no"));
            reqBody.put("notify_url", v3Config.getDomain() + CALL_BACK_URL);
            reqBody.put("amount", amount);
            reqBody.put("scene_info", sceneInfo);

            IJPayHttpResponse response = WxPayApi.v3(RequestMethodEnum.POST,
                    WxDomainEnum.CHINA.getDomain(),
                    BasePayApiEnum.H5_PAY.getUrl(),
                    v3Config.getMchId(),
                    v3Config.getSerialNo(),
                    null,
                    privateKey,
                    reqBody.toJSONString());

            logger.info("调用微信h5支付接口返回：{}", response.getBody());
            Map<String, String> result = new HashMap<>();
            result.put("trade_type", TradeType.MWEB.getTradeType());
            JSONObject respJson = JSONObject.parseObject(response.getBody());
            String h5Url = respJson.getString("h5_url");
            if (StringUtil.isNotEmpty(h5Url)) {
                result.put("return_code", "SUCCESS");
                result.put("result_code", "SUCCESS");
                // v3 返回 h5_url，沿用 v2 的 mweb_url 键名以免前端改动
                result.put("mweb_url", h5Url);
                result.put("h5_url", h5Url);
            } else {
                result.put("return_code", "FAIL");
                result.put("result_code", "FAIL");
                result.put("return_msg", respJson.getString("message"));
                logger.error("微信h5支付下单失败：{}", response.getBody());
                throw new RuntimeException(respJson.getString("message"));
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
     * @return
     * */
    private WxPayApiConfig getApiConfig(Integer storeId, String platform) {
        WxPayApiConfig apiConfig;

        String mchId = wxPayBean.getMchId();
        String apiV2 = wxPayBean.getApiV2();
        String certPath = wxPayBean.getCertPath();
        String appId = wxPayBean.getAppId();
        // 微信支付 APIv3 配置，证书取上传后的文件路径
        String apiV3 = wxPayBean.getApiV3();
        String certSerialNo = wxPayBean.getCertSerialNo();
        String privateKeyPath = wxPayBean.getPrivateKeyPath();
        String payPublicKeyPath = wxPayBean.getPayPublicKeyPath();

        MtStore mtStore = storeService.queryStoreById(storeId);
        logger.info("微信支付店铺信息：{}", JsonUtil.toJSONString(mtStore));
        // 门店需配齐 APIv3 全部要素才启用门店级配置，否则整组回落 configure 全局配置。
        // 逐项回落会让商户号与证书来自不同配置，签名必然失败且报错难以定位
        boolean useStoreConfig = mtStore != null
                && StringUtil.isNotEmpty(mtStore.getWxMchId())
                && StringUtil.isNotEmpty(mtStore.getWxApiV3())
                && StringUtil.isNotEmpty(mtStore.getWxCertSerialNo())
                && StringUtil.isNotEmpty(mtStore.getWxPrivateKeyPath())
                && StringUtil.isNotEmpty(mtStore.getWxPayPublicKeyPath());
        if (useStoreConfig) {
            logger.info("使用门店级支付配置，mchId = {}", mtStore.getWxMchId());
            mchId = mtStore.getWxMchId();
            apiV3 = mtStore.getWxApiV3();
            certSerialNo = mtStore.getWxCertSerialNo();
            String basePath = env.getProperty("images.root");
            privateKeyPath = basePath + mtStore.getWxPrivateKeyPath();
            payPublicKeyPath = basePath + mtStore.getWxPayPublicKeyPath();
            MtMerchant mtMerchant = merchantService.queryMerchantById(mtStore.getMerchantId());
            if (mtMerchant != null && StringUtil.isNotEmpty(mtMerchant.getWxAppId())) {
                appId = mtMerchant.getWxAppId();
            }
        } else if (mtStore != null) {
            logger.info("门店未配齐 APIv3 支付参数，回落至全局配置，storeId = {}", storeId);
        }

        apiConfig = WxPayApiConfig.builder()
                   .appId(appId)
                   .mchId(mchId)
                   .partnerKey(apiV2)
                   .certPath(certPath)
                   .domain(wxPayBean.getDomain())
                   .build();

        // 微信内h5公众号支付
        if (platform.equals(PlatformTypeEnum.H5.getCode()) || StringUtil.isBlank(appId)) {
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

        // 装配 APIv3 配置
        V3_CONFIG_HOLDER.set(WxPayV3Config.builder()
                .appId(apiConfig.getAppId())
                .mchId(mchId)
                .serialNo(certSerialNo)
                .apiV3(apiV3)
                .privateKeyPath(privateKeyPath)
                .payPublicKeyPath(payPublicKeyPath)
                .domain(wxPayBean.getDomain())
                .build());

        return apiConfig;
    }

    /**
     * 获取当前线程的微信支付 APIv3 配置
     * */
    private WxPayV3Config getV3Config() {
        return V3_CONFIG_HOLDER.get();
    }

    /**
     * 获取商户API私钥
     *
     * @param v3Config APIv3配置
     * @return 商户API私钥
     * */
    private PrivateKey getV3PrivateKey(WxPayV3Config v3Config) throws Exception {
        return PayKit.getPrivateKeyByKeyContent(readCertFile(v3Config.getPrivateKeyPath()), AuthTypeEnum.RSA.getCode());
    }

    /**
     * 读取证书文件内容
     *
     * @param path 证书文件路径
     * @return 证书 PEM 内容
     * */
    private String readCertFile(String path) throws Exception {
        if (StringUtil.isEmpty(path)) {
            throw new BusinessCheckException("微信支付证书未配置");
        }
        File certFile = new File(path);
        if (!certFile.exists()) {
            logger.error("微信支付证书文件不存在：{}", path);
            throw new BusinessCheckException("微信支付证书文件不存在");
        }
        return new String(Files.readAllBytes(certFile.toPath()), StandardCharsets.UTF_8);
    }
}
