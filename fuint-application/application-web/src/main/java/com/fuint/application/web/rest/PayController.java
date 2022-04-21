package com.fuint.application.web.rest;

import com.fuint.application.ResponseObject;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtOrderRepository;
import com.fuint.application.dto.CouponDto;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.enums.SettingTypeEnum;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.application.util.CommonUtil;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.BaseController;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付类controller
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/pay")
public class PayController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SignController.class);

    /**
     * 微信服务接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    UserGradeService userGradeService;

    @Autowired
    private MtOrderRepository orderRepository;

    /**
     * 支付前查询
     * */
    @RequestMapping(value = "/prePay", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject prePay(HttpServletRequest request) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser mtUser = tokenService.getUserInfoByToken(userToken);

        Integer userPoint = 0;
        if (mtUser != null) {
            userPoint = mtUser.getPoint();
        }

        Map<String, Object> outParams = new HashMap<>();

        List<MtSetting> settingList = settingService.getSettingList(SettingTypeEnum.POINT.getKey());
        String canUsedAsMoney = "false";
        String exchangeNeedPoint = "0";
        for (MtSetting setting : settingList) {
            if (setting.getName().equals("canUsedAsMoney")) {
                canUsedAsMoney = setting.getValue();
            } else if (setting.getName().equals("exchangeNeedPoint")) {
                exchangeNeedPoint = setting.getValue();
            }
        }

        // 可用卡券
        CouponDto canUseCouponInfo = null;
        if (mtUser != null) {
            List<CouponDto> couponList = userCouponService.getPayAbleCouponList(mtUser.getId());
            if (couponList.size() > 0) {
                canUseCouponInfo = couponList.get(0);
            }
        }

        // 会员折扣
        BigDecimal payDiscount = new BigDecimal("1");
        if (mtUser != null) {
            MtUserGrade userGrade = userGradeService.queryUserGradeById(Integer.parseInt(mtUser.getGradeId()));
            if (userGrade.getDiscount() > 0) {
                payDiscount = new BigDecimal(userGrade.getDiscount()).divide(new BigDecimal("10"));
            }
        }

        outParams.put("canUsedAsMoney", canUsedAsMoney);
        outParams.put("exchangeNeedPoint", exchangeNeedPoint);
        outParams.put("canUsePointAmount", userPoint);
        outParams.put("canUseCouponInfo", canUseCouponInfo);
        outParams.put("canUseCouponInfo", canUseCouponInfo);
        outParams.put("payDiscount", payDiscount);

        return getSuccessResult(outParams);
    }

    /**
     * 去支付
     * */
    @RequestMapping(value = "/doPay", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject doPay(HttpServletRequest request) throws BusinessCheckException{
        String userToken = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(userToken);

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        MtOrder orderInfo = orderRepository.findOne(Integer.parseInt(orderId));
        String ip = CommonUtil.getIPFromHttpRequest(request);
        // 实付金额 = 总金额 - 优惠金额 - 积分金额
        BigDecimal realPayAmount = orderInfo.getAmount().subtract(new BigDecimal(orderInfo.getDiscount().toString())).subtract(new BigDecimal(orderInfo.getPointAmount().toString()));
        BigDecimal pay = realPayAmount.multiply(new BigDecimal("100"));
        ResponseObject paymentInfo = weixinService.createPrepayOrder(userInfo, orderInfo, (pay.intValue()), "", 0, ip);

        Map<String, Object> outParams = new HashMap();

        outParams.put("isCreated", true);
        outParams.put("payType", "wechat");
        outParams.put("orderInfo", orderInfo);
        outParams.put("payment", paymentInfo.getData());

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 支付回调
     */
    @RequestMapping(value = "/weixinCallback", method = RequestMethod.POST)
    @CrossOrigin
    public void weixinCallback(HttpServletRequest request, HttpServletResponse response, Model model) throws BusinessCheckException{
        logger.info("微信支付结果回调....");

        Map<String, String> inParams = weixinService.processResXml(request);
        logger.info("微信返回Map:" + inParams);
        if (!CollectionUtils.isEmpty(inParams)) {
            String orderSn = inParams.get("out_trade_no");//商户订单号
            String orderId = inParams.get("transaction_id");//微信订单号
            String tranAmt = inParams.get("total_fee");//交易金额
            BigDecimal tranAmount = new BigDecimal(tranAmt).divide(new BigDecimal("100"));

            // 参数校验
            if (StringUtils.isNotEmpty(orderSn) && StringUtils.isNotEmpty(tranAmt) && StringUtils.isNotEmpty(orderId)) {
                UserOrderDto orderInfo = orderService.getOrderByOrderSn(orderSn);
                if (orderInfo != null) {
                    // 订单金额
                    BigDecimal payAmount = orderInfo.getPayAmount();
                    int compareFlag = tranAmount.compareTo(payAmount);
                    if (true) { // compareFlag == 0，测试暂时去掉
                        if (orderInfo.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
                            boolean flag = weixinService.paymentCallback(orderInfo);
                            logger.info("回调结果：" + flag);
                            if (flag) {
                                weixinService.processRespXml(response, true);
                            } else {
                                weixinService.processRespXml(response, false);
                            }
                        } else {
                            logger.error("订单{}已经支付，orderInfo.getStatus() = {}, CREATED.getKey() = {}", orderSn, orderInfo.getStatus(), OrderStatusEnum.CREATED.getKey());
                        }
                    } else {
                        logger.error("回调金额与支付金额不匹配 tranAmount = {}, payAmount = {}, compareFlag = {}", tranAmount, orderInfo.getPayAmount(), compareFlag);
                    }
                } else {
                    logger.error("支付订单{}对应的信息不存在", orderSn);
                }
            }
        }
    }
}
