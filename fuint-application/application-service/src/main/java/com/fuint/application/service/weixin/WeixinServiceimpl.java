package com.fuint.application.service.weixin;

import com.fuint.application.BaseService;
import com.fuint.application.dao.entities.MtUser;
import com.fuint.application.util.TimeUtils;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.fuint.application.config.WXPayConfigImpl;
import com.fuint.application.ResponseObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import weixin.popular.util.JsonUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinServiceimpl extends BaseService implements WeixinService {
    private static final Logger logger = LoggerFactory.getLogger(WeixinServiceimpl.class);

    @Resource
    private WXPayConfigImpl wxPayConfigImpl;

    @Override
    public ResponseObject createPrepayOrder(MtUser userInfo, String memberId, String goodsInfo, Integer payAmount, Integer giveAmount) {
        logger.debug("WeixinService createPrepayOrder inParams memberId={} payAmount={} giveAmount={} goodsInfo={}",
                memberId, payAmount, giveAmount, goodsInfo);

        //@todo 1.生成充值订单

        //2. 调用微信接口生成预支付订单
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put("body", goodsInfo);
        reqData.put("out_trade_no", "202105162i434530");
        reqData.put("device_info", "");
        reqData.put("fee_type", "CNY");
        reqData.put("total_fee", payAmount.toString());
        reqData.put("spbill_create_ip", "123.12.12.123");
        reqData.put("notify_url", wxPayConfigImpl.getCallbackUrl());
        reqData.put("trade_type", "JSAPI");
        reqData.put("openid", "ofK10v7qk3RYnOcS8hNE-XDy18Gk");

        Map<String, String> respData = this.unifiedOrder(reqData);
        if (respData == null) {
            logger.error("微信支付接口调用异常......");
            return getFailureResult(3000, "微信支付接口调用异常");
        }

        //@todo 3.记录支付接口请求/响应参数

        Map<String, String> outParmas = new HashMap<>();

        //4.更新预支付订单号
        if (respData.get("return_code").equals("SUCCESS")) {
            String prepayId = respData.get("prepay_id");

            //组织返回参数
            String appId = respData.get("appid");
            String nonceStr = respData.get("nonce_str");

            outParmas.put("appId", appId);
            outParmas.put("timeStamp", String.valueOf(TimeUtils.timeStamp()));

            outParmas.put("nonceStr", nonceStr);
            outParmas.put("package", "prepay_id=" + prepayId);
            outParmas.put("signType", "MD5");
            try {
                String sign = WXPayUtil.generateSignature(outParmas, wxPayConfigImpl.getKey());
                outParmas.put("paySign", sign);
                outParmas.put("prepayId", "1000");
            } catch (Exception e) {
                //签名失败
                logger.error(e.getMessage(), e);
                return getFailureResult(3000, "微信支付签名失败");
            }
        } else {
            logger.error("微信支付接口返回状态失败......");
            return getFailureResult(3000, "微信支付接口返回状态失败");
        }

        ResponseObject responseObject = getSuccessResult(outParmas);
        logger.debug("WXService createPrepayOrder outParams {}", responseObject.toString());
        return responseObject;
    }

    @Override
    public boolean paymentCallback(MtUser userPayment) {
        // @todo 1.更新订单状态

        // @todo 2.发卡券
        return true;
    }

    public Map<String, String> processResXml(HttpServletRequest request) {
        InputStream inStream = null;
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            inStream = request.getInputStream();
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }

            String result = new String(outSteam.toByteArray(), "utf-8");
            logger.info("微信支付回调入参报文{}", result);

            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            String returnCode = resultMap.get("return_code");
            if (StringUtils.isNotEmpty(returnCode) && returnCode.equals("SUCCESS")) {
                boolean flag = WXPayUtil.isSignatureValid(resultMap, wxPayConfigImpl.getKey());
                if (!flag) {
                    logger.error("微信支付回调接口验签失败");
                    return null;
                }
                return resultMap;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (outSteam != null) {
                try {
                    outSteam.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public void processRespXml(HttpServletResponse response, boolean flag){
        Map<String,String> respData = new HashMap<String,String>();
        if (flag) {
            respData.put("return_code", "SUCCESS");
            respData.put("return_msg", "OK");
        }else{
            respData.put("return_code", "FAIL");
            respData.put("return_msg", "FAIL");
        }
        OutputStream outputStream = null;
        try {
            String respXml = WXPayUtil.mapToXml(respData);
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
                    outputStream = null;
                }
            }
        }
    }

    private Map<String, String> unifiedOrder(Map<String, String> reqData) {
        try {
            logger.info("调用微信支付下单接口入参{}", JsonUtil.toJSONString(reqData));

            WXPay wxPay = new WXPay(wxPayConfigImpl);
            Map<String, String> respMap = wxPay.unifiedOrder(reqData);

            logger.info("调用微信支付下单接口返回{}", JsonUtil.toJSONString(respMap));
            return respMap;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
