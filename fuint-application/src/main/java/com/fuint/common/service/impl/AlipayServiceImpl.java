package com.fuint.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.fuint.common.bean.AliPayBean;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.OrderUserDto;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.model.OrderQueryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * 支付宝相关接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class AlipayServiceImpl implements AlipayService {

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
    private MemberService memberService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private StoreService storeService;

    @Autowired
    AliPayBean aliPayBean;

    /**
     * 创建支付订单
     * @return
     * */
    @Override
    @Transactional
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException {
        logger.info("AlipayService createPrepayOrder inParams userInfo={} payAmount={} giveAmount={} goodsInfo={}", userInfo, payAmount, giveAmount, orderInfo);

        String goodsInfo = orderInfo.getOrderSn();
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            goodsInfo = OrderTypeEnum.PRESTORE.getValue();
        }

        // 更新支付金额
        BigDecimal payAmount1 = new BigDecimal(payAmount).divide(new BigDecimal("100"));
        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderInfo.getId());
        reqDto.setPayAmount(payAmount1);
        reqDto.setPayType(orderInfo.getPayType());
        orderService.updateOrder(reqDto);

        String notifyUrl = aliPayBean.getDomain();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setSubject(goodsInfo);
        model.setTotalAmount(payAmount.toString());
        model.setStoreId(orderInfo.getStoreId().toString());
        model.setTimeoutExpress("5m");
        model.setOutTradeNo(orderInfo.getOrderSn());
        String qrCode = "";
        try {
            String resultStr = AliPayApi.tradePrecreatePayToResponse(model, notifyUrl).getBody();
            JSONObject jsonObject = JSONObject.parseObject(resultStr);
            qrCode = jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> respData = new HashMap<>();
        respData.put("qrCode", qrCode);

        ResponseObject responseObject = new ResponseObject(200, "支付宝支付接口返回成功", respData);
        logger.info("AlipayService createPrepayOrder outParams {}", responseObject.toString());

        return responseObject;
    }

    /**
     * 支付回调
     * @return
     * */
    @Override
    @Transactional
    public Boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException {
        logger.info("AlipayService paymentCallback outParams {}", orderInfo.toString());

        // 更新订单状态为已支付
        Boolean isPay = orderService.setOrderPayed(orderInfo.getId());
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
                BigDecimal point = orderInfo.getPayAmount().divide(new BigDecimal(needPayAmountInt), BigDecimal.ROUND_CEILING, 2);
                pointNum = Math.ceil(point.doubleValue());
            }

            logger.info("AlipayService paymentCallback Point orderSn = {} , pointNum ={}", orderInfo.getOrderSn(), pointNum);

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

        logger.info("AlipayService paymentCallback Success orderSn {}", orderInfo.getOrderSn());
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
     * 获取支付配置
     * @param storeId
     * @param platform
     * */
    private WxPayApiConfig getApiConfig(Integer storeId, String platform) throws BusinessCheckException {
        WxPayApiConfig apiConfig;
        MtStore mtStore = storeService.queryStoreById(storeId);

        apiConfig = WxPayApiConfig.builder()
                   .appId(aliPayBean.getAppId())
                   .domain(aliPayBean.getDomain())
                   .build();

        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(apiConfig);
        return apiConfig;
    }
}
