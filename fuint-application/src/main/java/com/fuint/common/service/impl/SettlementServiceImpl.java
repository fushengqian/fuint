package com.fuint.common.service.impl;

import com.fuint.common.dto.AccountInfo;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.UserInfo;
import com.fuint.common.enums.*;
import com.fuint.common.param.SettlementParam;
import com.fuint.common.service.*;
import com.fuint.common.util.CommonUtil;
import com.fuint.common.util.DateUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.*;
import com.fuint.utils.PropertiesUtil;
import com.fuint.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单结算相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class SettlementServiceImpl implements SettlementService {

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
     * 店铺服务接口
     */
    @Autowired
    private StoreService storeService;

    /**
     * 员工服务接口
     * */
    @Autowired
    private StaffService staffService;

    /**
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    /**
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 会员等级接口
     * */
    @Autowired
    UserGradeService userGradeService;

    /**
     * 会员卡券服务
     * */
    @Autowired
    private UserCouponService userCouponService;

    /**
     * 支付服务接口
     * */
    @Autowired
    private PaymentService paymentService;

    /**
     * 订单结算
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> doSubmit(HttpServletRequest request, SettlementParam param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        String cartIds = param.getCartIds() == null ? "" : param.getCartIds();
        Integer targetId = param.getTargetId() == null ? 0 : Integer.parseInt(param.getTargetId()); // 储值卡、升级等级必填
        String selectNum = param.getSelectNum() == null ? "" : param.getSelectNum(); // 储值卡必填
        String remark = param.getRemark() == null ? "" : param.getRemark();
        String type = param.getType() == null ? "" : param.getType(); // 订单类型
        String payAmount = param.getPayAmount() == null ? "0" : StringUtil.isEmpty(param.getPayAmount()) ? "0" : param.getPayAmount(); // 支付金额
        Integer usePoint = param.getUsePoint() == null ? 0 : param.getUsePoint(); // 使用积分数量
        Integer couponId = param.getCouponId() == null ? 0 : param.getCouponId(); // 会员卡券ID
        String payType = param.getPayType() == null ? PayTypeEnum.JSAPI.getKey() : param.getPayType();
        String authCode = param.getAuthCode() == null ? "" : param.getAuthCode();
        Integer userId = param.getUserId() == null ? 0 : param.getUserId(); // 指定下单会员 eg:收银功能
        String cashierPayAmount = param.getCashierPayAmount() == null ? "" : param.getCashierPayAmount(); // 收银台实付金额
        String cashierDiscountAmount = param.getCashierDiscountAmount() == null ? "" : param.getCashierDiscountAmount(); // 收银台优惠金额
        Integer goodsId = param.getGoodsId() == null ? 0 : param.getGoodsId(); // 立即购买商品ID
        Integer skuId = param.getSkuId() == null ? 0 : param.getSkuId(); // 立即购买商品skuId
        Integer buyNum = param.getBuyNum() == null ? 1 : param.getBuyNum(); // 立即购买商品数量
        String orderMode = param.getOrderMode()== null ? OrderModeEnum.ONESELF.getKey() : param.getOrderMode(); // 订单模式(配送or自取)
        Integer orderId = param.getOrderId() == null ? null : param.getOrderId(); // 订单ID

        MtSetting config = settingService.querySettingByName(OrderSettingEnum.IS_CLOSE.getKey());
        if (config != null && config.getValue().equals("true")) {
            throw new BusinessCheckException("系统已关闭交易功能，请稍后再试！");
        }

        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        MtUser userInfo = null;
        if (loginInfo != null) {
            userInfo = memberService.queryMemberById(loginInfo.getId());
        }

        // 后台管理员或店员操作
        String operator = null;
        Integer staffId = 0;
        String isVisitor = YesOrNoEnum.NO.getKey();
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo != null) {
            operator = accountInfo.getAccountName();
            staffId = accountInfo.getStaffId() == null ? 0 : accountInfo.getStaffId();
            storeId = accountInfo.getStoreId();
            if (storeId <= 0) {
                MtStore mtStore = storeService.getDefaultStore(merchantNo);
                if (mtStore != null) {
                    storeId = mtStore.getId();
                }
            }
            if (userId < 1) {
                isVisitor = YesOrNoEnum.YES.getKey();
            }
        }

        if (userInfo == null) {
            MtUser user = memberService.getCurrentUserInfo(request, userId, token);
            if (user != null) {
                userInfo = memberService.queryMemberById(user.getId());
            }
        } else {
            MtStaff mtStaff = staffService.queryStaffByUserId(userInfo.getId());
            if (mtStaff != null) {
                operator = mtStaff.getRealName();
            }
        }

        // 收银台通过手机号自动注册会员信息
        if ((userInfo == null || StringUtil.isEmpty(token))) {
            String mobile = param.getMobile() == null ? "" : param.getMobile();
            if (StringUtil.isNotEmpty(operator) && StringUtil.isNotEmpty(mobile)) {
                userInfo = memberService.queryMemberByMobile(mobile);
                // 自动注册会员
                if (userInfo == null) {
                    userInfo = memberService.addMemberByMobile(mobile);
                }
            }
        }

        if (userInfo == null) {
            if (StringUtil.isNotEmpty(operator)) {
                throw new BusinessCheckException("该管理员还未关联店铺员工");
            } else {
                throw new BusinessCheckException("请先登录");
            }
        }

        if (userId <= 0) {
            userId = userInfo.getId();
        } else {
            if (StringUtil.isNotEmpty(operator)) {
                userInfo = memberService.queryMemberById(userId);
            }
        }
        param.setUserId(userId);

        // 订单所属店铺
        if (storeId < 1) {
            if (userInfo.getStoreId() > 0) {
                storeId = userInfo.getStoreId();
            }
        }

        // 生成订单数据
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setRemark(remark);
        orderDto.setUserId(userId);
        orderDto.setStoreId(storeId);
        orderDto.setType(type);
        orderDto.setGoodsId(goodsId);
        orderDto.setSkuId(skuId);
        orderDto.setBuyNum(buyNum);
        orderDto.setOrderMode(orderMode);
        orderDto.setOperator(operator);
        orderDto.setPayType(payType);
        orderDto.setCouponId(0);
        orderDto.setStaffId(staffId);
        orderDto.setIsVisitor(isVisitor);
        orderDto.setPlatform(platform);

        MtSetting pointSetting = settingService.querySettingByName(PointSettingEnum.CAN_USE_AS_MONEY.getKey());
        // 使用积分数量
        if (pointSetting != null && pointSetting.getValue().equals("true")) {
            orderDto.setUsePoint(usePoint);
        } else {
            orderDto.setUsePoint(0);
            usePoint = 0;
        }

        orderDto.setPointAmount(new BigDecimal("0"));
        orderDto.setDiscount(new BigDecimal("0"));
        orderDto.setPayAmount(new BigDecimal("0"));
        orderDto.setAmount(new BigDecimal("0"));
        orderDto.setCartIds(cartIds);

        // 储值卡的订单
        if (orderDto.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            orderDto.setCouponId(targetId);
            String orderParam = "";
            BigDecimal totalAmount = new BigDecimal(0);

            MtCoupon couponInfo = couponService.queryCouponById(targetId);
            String inRule = couponInfo.getInRule();
            String[] selectNumArr = selectNum.split(",");
            String[] ruleArr = inRule.split(",");
            for (int i = 0; i < ruleArr.length; i++) {
                String item = ruleArr[i] + "_" + (StringUtil.isNotEmpty(selectNumArr[i]) ? selectNumArr[i] : 0);
                String[] itemArr = item.split("_");
                // 预存金额
                BigDecimal price = new BigDecimal(itemArr[0]);
                // 预存数量
                BigDecimal num = new BigDecimal(selectNumArr[i]);
                BigDecimal amount = price.multiply(num);
                totalAmount = totalAmount.add(amount);
                orderParam = StringUtil.isEmpty(orderParam) ?  item : orderParam + ","+item;
            }

            orderDto.setParam(orderParam);
            orderDto.setAmount(totalAmount);
            payAmount = totalAmount.toString();
        }

        // 付款订单
        if (orderDto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) {
            orderDto.setAmount(new BigDecimal(payAmount));
            orderDto.setPayAmount(new BigDecimal(payAmount));
            orderDto.setDiscount(new BigDecimal("0"));
        }

        // 会员升级订单
        if (orderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            orderDto.setParam(targetId.toString());
            orderDto.setCouponId(couponId);
            MtUserGrade userGrade = userGradeService.queryUserGradeById(targetId, orderDto.getUserId());
            if (userGrade != null) {
                orderDto.setRemark("付费升级" + userGrade.getName());
                orderDto.setAmount(new BigDecimal(userGrade.getCatchValue().toString()));
            }
        }

        // 商品订单
        if (orderDto.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
            orderDto.setCouponId(couponId);
        }

        // 商品订单且配送要加上配送费用
        if (orderDto.getType().equals(OrderTypeEnum.GOOGS.getKey()) && orderDto.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey())) {
            MtSetting mtSetting = settingService.querySettingByName(OrderSettingEnum.DELIVERY_FEE.getKey());
            if (mtSetting != null && StringUtil.isNotEmpty(mtSetting.getValue())) {
                BigDecimal deliveryFee = new BigDecimal(mtSetting.getValue());
                if (deliveryFee.compareTo(new BigDecimal("0")) > 0) {
                    orderDto.setDeliveryFee(deliveryFee);
                }
            }
        }

        // 使用积分抵扣
        if (usePoint > 0) {
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
            // 是否可以使用积分，并且积分数量足够
            if (canUsedAsMoney.equals("true") && Float.parseFloat(exchangeNeedPoint) > 0 && (userInfo.getPoint() >= usePoint)) {
                orderDto.setUsePoint(usePoint);
                orderDto.setPointAmount(new BigDecimal(usePoint).divide(new BigDecimal(exchangeNeedPoint), BigDecimal.ROUND_CEILING, 3));
                if (orderDto.getPayAmount().compareTo(orderDto.getPointAmount()) > 0) {
                    orderDto.setPayAmount(orderDto.getPayAmount().subtract(orderDto.getPointAmount()));
                } else {
                    orderDto.setPayAmount(new BigDecimal("0"));
                }
            }
        }

        // 首先生成订单，拿到订单ID
        MtOrder orderInfo;
        try {
            orderInfo = orderService.saveOrder(orderDto);
        } catch (BusinessCheckException e) {
            throw new BusinessCheckException(e.getMessage() == null ?  "生成订单失败" : e.getMessage());
        }

        orderDto.setId(orderInfo.getId());
        param.setOrderId(orderInfo.getId());

        // 收银台实付金额、优惠金额
        if ((StringUtil.isNotEmpty(cashierPayAmount) || StringUtil.isNotEmpty(cashierDiscountAmount)) && StringUtil.isNotEmpty(operator)) {
            OrderDto reqOrder = new OrderDto();
            reqOrder.setId(orderInfo.getId());
            if (orderInfo.getAmount().compareTo(new BigDecimal("0")) <= 0) {
                reqOrder.setAmount(new BigDecimal(cashierPayAmount).add(new BigDecimal(cashierDiscountAmount)));
            } else {
                reqOrder.setAmount(orderInfo.getAmount());
            }
            if (new BigDecimal(cashierDiscountAmount).compareTo(new BigDecimal("0")) > 0) {
                reqOrder.setDiscount(new BigDecimal(cashierDiscountAmount).add(orderInfo.getDiscount()));
            } else {
                reqOrder.setDiscount(orderInfo.getDiscount());
            }
            BigDecimal realPayAmount = reqOrder.getAmount().subtract(reqOrder.getDiscount());
            if (realPayAmount.compareTo(new BigDecimal("0")) < 0) {
                realPayAmount = new BigDecimal("0");
            }
            reqOrder.setPayAmount(realPayAmount);
            orderService.updateOrder(reqOrder);
            orderInfo = orderService.getOrderInfo(orderInfo.getId());
        }

        // 订单中使用卡券抵扣(付款订单、会员升级订单)
        if (couponId > 0 && (orderDto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) || orderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            if (orderDto.getAmount().compareTo(new BigDecimal("0")) > 0) {
                MtUserCoupon userCouponInfo = userCouponService.getUserCouponDetail(couponId);
                if (userCouponInfo != null) {
                    MtCoupon couponInfo = couponService.queryCouponById(userCouponInfo.getCouponId());
                    if (couponInfo != null) {
                        boolean isEffective = couponService.isCouponEffective(couponInfo);
                        if (isEffective && userCouponInfo.getUserId().equals(orderDto.getUserId())) {
                            // 优惠券，直接减去优惠券金额
                            if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                                // 检查是否会员升级专用卡券
                                boolean canUse = true;
                                if (couponInfo.getUseFor() != null && StringUtil.isNotEmpty(couponInfo.getUseFor())) {
                                    if (orderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
                                        if (!couponInfo.getUseFor().equals(CouponUseForEnum.MEMBER_GRADE.getKey())) {
                                            canUse = false;
                                        }
                                    }
                                }
                                if (canUse) {
                                    String useCode = couponService.useCoupon(couponId, orderDto.getUserId(), orderDto.getStoreId(), orderInfo.getId(), userCouponInfo.getAmount(), "核销");
                                    if (StringUtil.isNotEmpty(useCode)) {
                                        orderDto.setCouponId(couponId);
                                        orderDto.setDiscount(orderInfo.getDiscount().add(userCouponInfo.getAmount()));
                                        orderService.updateOrder(orderDto);
                                    }
                                }
                            } else if(couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                                // 储值卡，减去余额
                                BigDecimal useCouponAmount = userCouponInfo.getBalance();
                                if (orderInfo.getPayAmount().compareTo(userCouponInfo.getBalance()) <= 0) {
                                    useCouponAmount = orderInfo.getPayAmount();
                                }
                                try {
                                    String useCode = couponService.useCoupon(couponId, orderDto.getUserId(), orderDto.getStoreId(), orderInfo.getId(), useCouponAmount, "核销");
                                    if (StringUtil.isNotEmpty(useCode)) {
                                        orderDto.setCouponId(couponId);
                                        orderDto.setDiscount(orderInfo.getDiscount().add(useCouponAmount));
                                        orderDto.setPayAmount(orderInfo.getPayAmount().subtract(useCouponAmount));
                                        orderService.updateOrder(orderDto);
                                    }
                                } catch (BusinessCheckException e) {
                                    throw new BusinessCheckException(e.getMessage() == null ?  "生成订单失败" : e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }

        // 生成支付订单
        orderInfo = orderService.getOrderInfo(orderInfo.getId());
        String ip = CommonUtil.getIPFromHttpRequest(request);
        BigDecimal realPayAmount = orderInfo.getAmount().subtract(new BigDecimal(orderInfo.getDiscount().toString())).subtract(new BigDecimal(orderInfo.getPointAmount().toString())).add(orderInfo.getDeliveryFee());

        // 支付类的订单，检查余额是否充足
        if (type.equals(OrderTypeEnum.PAYMENT.getKey()) && payType.equals(PayTypeEnum.BALANCE.getKey())) {
            if (userInfo.getBalance() == null || realPayAmount.compareTo(userInfo.getBalance()) > 0) {
                throw new BusinessCheckException("会员余额不足");
            }
            if (StringUtil.isNotEmpty(cashierPayAmount)) {
                if (userInfo.getBalance() == null || new BigDecimal(cashierPayAmount).compareTo(userInfo.getBalance()) > 0) {
                    throw new BusinessCheckException("会员余额不足");
                }
            }
        }

        ResponseObject paymentInfo = null;
        String errorMessage = "";

        // 应付金额大于0才提交微信支付
        if (realPayAmount.compareTo(new BigDecimal("0")) > 0) {
            if (payType.equals(PayTypeEnum.CASH.getKey()) && StringUtil.isNotEmpty(operator)) {
                // 收银台现金支付，更新为已支付
                orderService.setOrderPayed(orderInfo.getId(), null);
            } else if(payType.equals(PayTypeEnum.BALANCE.getKey())) {
                // 余额支付
                MtBalance balance = new MtBalance();
                balance.setMobile(userInfo.getMobile());
                balance.setOrderSn(orderInfo.getOrderSn());
                balance.setUserId(userInfo.getId());
                BigDecimal balanceAmount = realPayAmount.subtract(realPayAmount).subtract(realPayAmount);
                balance.setAmount(balanceAmount);
                boolean isPay = balanceService.addBalance(balance);
                if (isPay) {
                    orderService.setOrderPayed(orderInfo.getId(), realPayAmount);
                } else {
                    errorMessage = PropertiesUtil.getResponseErrorMessageByCode(5001);
                }
            } else {
                BigDecimal wxPayAmount = realPayAmount.multiply(new BigDecimal("100"));
                // 扫码支付，先返回不处理，后面拿到支付二维码再处理
                if ((payType.equals(PayTypeEnum.MICROPAY.getKey()) || payType.equals(PayTypeEnum.ALISCAN.getKey())) && StringUtil.isEmpty(authCode)) {
                    paymentInfo = new ResponseObject(200, "请求成功", new HashMap<>());
                } else {
                    paymentInfo = paymentService.createPrepayOrder(userInfo, orderInfo, (wxPayAmount.intValue()), authCode, 0, ip, platform);
                }
                if (paymentInfo.getData() == null) {
                    errorMessage = PropertiesUtil.getResponseErrorMessageByCode(3000);
                }
            }
        } else {
            // 应付金额是0，直接更新为已支付
            orderService.setOrderPayed(orderInfo.getId(), null);
        }

        orderInfo = orderService.getOrderInfo(orderInfo.getId());
        Map<String, Object> outParams = new HashMap();
        outParams.put("isCreated", true);
        outParams.put("orderInfo", orderInfo);

        if (paymentInfo != null) {
            outParams.put("payment", paymentInfo.getData());
            outParams.put("payType", payType);
        } else {
            outParams.put("payment", null);
            outParams.put("payType", "BALANCE");
        }

        // 1分钟后发送小程序订阅消息
        Date nowTime = new Date();
        Date sendTime = new Date(nowTime.getTime() + 60000);
        Map<String, Object> params = new HashMap<>();
        String dateTime = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm");
        params.put("time", dateTime);
        params.put("orderSn", orderInfo.getOrderSn());
        params.put("remark", "您的订单已生成，请留意~");
        weixinService.sendSubscribeMessage(userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.ORDER_CREATED.getKey(), "pages/order/index", params, sendTime);

        if (StringUtil.isNotEmpty(errorMessage)) {
            throw new BusinessCheckException(errorMessage);
        } else {
            return outParams;
        }
    }
}
