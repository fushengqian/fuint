package com.fuint.common.service.impl;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.PayTypeEnum;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtOrderMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付相关接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Resource
    private MtOrderMapper mtOrderMapper;

    @Autowired
    private WeixinService weixinService;

    /**
     * 会员服务接口
     * */
    @Autowired
    private MemberService memberService;

    /**
     * 订单服务接口
     * */
    @Autowired
    private OrderService orderService;

    /**
     * 余额服务接口
     * */
    @Autowired
    private BalanceService balanceService;

    /**
     * 创建支付订单
     * @return
     * */
    @Override
    public ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform) throws BusinessCheckException {
        logger.info("PaymentService createPrepayOrder inParams userInfo={} payAmount={} giveAmount={} goodsInfo={}", userInfo, payAmount, giveAmount, orderInfo);
        ResponseObject responseObject = weixinService.createPrepayOrder(userInfo, orderInfo, payAmount, authCode, giveAmount, ip, platform);
        logger.info("PaymentService createPrepayOrder outParams {}", responseObject.toString());
        return responseObject;
    }

    /**
     * 支付回调
     * @return
     * */
    @Override
    @Transactional
    public Boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException {
        Boolean result = weixinService.paymentCallback(orderInfo);
        if (!result) {
            logger.error("PaymentService paymentCallback error orderSn {}", orderInfo.getOrderSn());
        }
        logger.info("PaymentService paymentCallback Success orderSn {}", orderInfo.getOrderSn());
        return true;
    }

    /**
     * 订单支付
     * */
    @Override
    @Transactional
    public Map<String, Object> doPay(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        String payType = request.getParameter("payType") == null ? PayTypeEnum.JSAPI.getKey() : request.getParameter("payType");
        String cashierPayAmount = request.getParameter("cashierPayAmount") == null ? "" : request.getParameter("cashierPayAmount"); // 收银台实付金额
        String cashierDiscountAmount = request.getParameter("cashierDiscountAmount") == null ? "" : request.getParameter("cashierDiscountAmount"); // 收银台优惠金额
        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        String orderId = request.getParameter("orderId");
        String userId = request.getParameter("userId");
        String authCode = request.getParameter("authCode");
        if (StringUtil.isEmpty(orderId)) {
            throw new BusinessCheckException("订单不能为空");
        }

        MtOrder orderInfo = mtOrderMapper.selectById(Integer.parseInt(orderId));
        if (orderInfo == null) {
            throw new BusinessCheckException("该订单不存在");
        }
        MtUser mtUser = null;
        if (loginInfo != null) {
            mtUser = memberService.queryMemberById(loginInfo.getId());
        }

        // 收银员操作
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (loginInfo == null && accountInfo != null) {
            // 游客订单绑定到会员
            if (orderInfo.getIsVisitor().equals(YesOrNoEnum.YES.getKey()) && StringUtil.isNotEmpty(userId)) {
                mtUser = memberService.queryMemberById(Integer.parseInt(userId));
                orderInfo.setUserId(Integer.parseInt(userId));
                orderInfo.setIsVisitor(YesOrNoEnum.NO.getKey());
                orderService.updateOrder(orderInfo);
            } else {
                mtUser = memberService.queryMemberById(orderInfo.getUserId());
            }
        }

        if (mtUser == null) {
            throw new BusinessCheckException("登录信息失效");
        }

        if (accountInfo != null && StringUtil.isNotEmpty(cashierPayAmount) && StringUtil.isNotEmpty(cashierDiscountAmount)) {
            orderInfo.setDiscount(new BigDecimal(cashierDiscountAmount));
            if (loginInfo == null) {
                MtUser user = memberService.queryMemberById(orderInfo.getUserId());
                if (user != null) {
                    loginInfo = new UserInfo();
                    loginInfo.setId(user.getId());
                }
            }
        }

        // 实付金额 = 总金额 - 优惠金额 - 积分金额
        BigDecimal realPayAmount = orderInfo.getAmount().subtract(new BigDecimal(orderInfo.getDiscount().toString())).subtract(new BigDecimal(orderInfo.getPointAmount().toString()));
        Object payment = null;
        if (payType.equals(PayTypeEnum.BALANCE.getKey())) {
            // 余额支付
            MtBalance balance = new MtBalance();
            balance.setMobile(mtUser.getMobile());
            balance.setOrderSn(orderInfo.getOrderSn());
            balance.setUserId(mtUser.getId());
            balance.setAmount(realPayAmount.subtract(realPayAmount).subtract(realPayAmount));
            boolean isPay = balanceService.addBalance(balance);
            if (isPay) {
                orderService.setOrderPayed(orderInfo.getId());
                OrderDto reqOrder = new OrderDto();
                reqOrder.setId(orderInfo.getId());
                reqOrder.setPayAmount(realPayAmount);
                reqOrder.setDiscount(orderInfo.getDiscount());
                reqOrder.setPayType(PayTypeEnum.BALANCE.getKey());
                if (accountInfo != null) {
                    reqOrder.setOperator(accountInfo.getAccountName());
                }
                orderService.updateOrder(reqOrder);
                orderInfo = orderService.getOrderInfo(orderInfo.getId());
            } else {
                throw new BusinessCheckException("会员余额不足");
            }
        } else if (payType.equals(PayTypeEnum.CASH.getKey()) && accountInfo != null) {
            // 现金支付
            OrderDto reqOrder = new OrderDto();
            reqOrder.setId(orderInfo.getId());
            reqOrder.setAmount(new BigDecimal(cashierPayAmount).add(new BigDecimal(cashierDiscountAmount)));
            reqOrder.setDiscount(new BigDecimal(cashierDiscountAmount));
            reqOrder.setPayAmount(new BigDecimal(cashierPayAmount));
            reqOrder.setPayTime(new Date());
            reqOrder.setPayType(PayTypeEnum.CASH.getKey());
            reqOrder.setOperator(accountInfo.getAccountName());
            orderService.updateOrder(reqOrder);
            orderService.setOrderPayed(orderInfo.getId());
            orderInfo = orderService.getOrderInfo(orderInfo.getId());
        } else {
            String ip = CommonUtil.getIPFromHttpRequest(request);
            BigDecimal pay = realPayAmount.multiply(new BigDecimal("100"));
            orderInfo.setPayType(payType);
            ResponseObject paymentInfo = createPrepayOrder(mtUser, orderInfo, (pay.intValue()), authCode, 0, ip, platform);
            if (paymentInfo.getData() == null) {
                throw new BusinessCheckException("抱歉，微信支付失败");
            }
            payment = paymentInfo.getData();
        }

        Map<String, Object> result = new HashMap();
        result.put("isCreated", true);
        result.put("payType", payType);
        result.put("orderInfo", orderInfo);
        result.put("payment", payment);

        return result;
    }
}
