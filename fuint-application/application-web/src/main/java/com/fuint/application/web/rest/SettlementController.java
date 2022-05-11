package com.fuint.application.web.rest;

import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtOrderRepository;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.enums.*;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.token.TokenService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.application.service.usergrade.UserGradeService;
import com.fuint.application.util.CommonUtil;
import com.fuint.base.shiro.util.ShiroUserHelper;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.ResponseObject;
import com.fuint.application.BaseController;
import com.fuint.base.shiro.ShiroUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结算中心接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@RestController
@RequestMapping(value = "/rest/settlement")
public class SettlementController extends BaseController {

    /**
     * Token服务接口
     */
    @Autowired
    private TokenService tokenService;

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
     * 卡券服务接口
     */
    @Autowired
    private CouponService couponService;

    /**
     * 微信服务接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 会员等级接口
     * */
    @Autowired UserGradeService userGradeService;

    /**
     * 会员卡券服务
     * */
    @Autowired
    private UserCouponService userCouponService;

    /**
     * 配置服务接口
     * */
    @Autowired
    private SettingService settingService;

    @Autowired
    private MtOrderRepository orderRepository;

    /**
     * 结算提交
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        MtUser userInfo = tokenService.getUserInfoByToken(token);

        String operator = null;
        ShiroUser shiroUser = ShiroUserHelper.getCurrentShiroUser();
        if (shiroUser != null) {
            operator = ShiroUserHelper.getCurrentShiroUser().getAcctName();
            if (userInfo == null) {
                userInfo = memberService.getCurrentUserInfo(0);
            }
        }

        // 后台操作自动注册会员信息
        if ((userInfo == null || StringUtils.isEmpty(token))) {
            String mobile = param.get("mobile") == null ? "" : param.get("mobile").toString();
            if (StringUtils.isNotEmpty(operator) && StringUtils.isNotEmpty(mobile)) {
                userInfo = memberService.queryMemberByMobile(mobile);
                // 自动注册会员
                if (userInfo == null) {
                    userInfo = memberService.addMemberByMobile(mobile);
                }
            }
        }

        if (userInfo == null) {
            return getFailureResult(1001);
        }

        String cartIds = param.get("cartIds") == null ? "" : param.get("cartIds").toString();
        Integer targetId = param.get("targetId") == null ? 0 : Integer.parseInt(param.get("targetId").toString()); // 预存卡、升级等级必填
        String selectNum = param.get("selectNum") == null ? "" : param.get("selectNum").toString(); // 预存卡必填
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String type = param.get("type") == null ? "" : param.get("type").toString();
        String payAmount = param.get("payAmount") == null ? "0" : StringUtils.isEmpty(param.get("payAmount").toString()) ? "0" : param.get("payAmount").toString(); // 支付金额
        Integer usePoint = param.get("usePoint") == null ? 0 : Integer.parseInt(param.get("usePoint").toString());
        Integer couponId = param.get("couponId") == null ? 0 : Integer.parseInt(param.get("couponId").toString());
        String payType = param.get("payType") == null ? "JSAPI" : param.get("payType").toString();
        String authCode = param.get("authCode") == null ? "" : param.get("authCode").toString();
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer userId = param.get("userId") == null ? 0 : (StringUtils.isNotEmpty(param.get("userId").toString()) ? Integer.parseInt(param.get("userId").toString()) : 0); // 指定下单会员 eg:收银功能
        String cashierPayAmount = param.get("cashierPayAmount") == null ? "" : param.get("cashierPayAmount").toString(); // 收银台实付金额
        String cashierDiscountAmount = param.get("cashierDiscountAmount") == null ? "" : param.get("cashierDiscountAmount").toString(); // 收银台优惠金额
        Integer goodsId = param.get("goodsId") == null ? 0 : Integer.parseInt(param.get("goodsId").toString()); // 立即购买商品ID
        Integer skuId = param.get("skuId") == null ? 0 : Integer.parseInt(param.get("skuId").toString()); // 立即购买商品skuId
        Integer buyNum = param.get("buyNum") == null ? 1 : Integer.parseInt(param.get("buyNum").toString()); // 立即购买商品数量
        String orderMode = param.get("orderMode") == null ? "" : param.get("orderMode").toString(); // 订单模式(配送or自取)
        Integer orderId = param.get("orderId") == null ? null : Integer.parseInt(param.get("orderId").toString()); // 订单ID

        if (userId <= 0) {
            userId = userInfo.getId();
        }
        param.put("userId", userId);

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
        orderDto.setUsePoint(usePoint);
        orderDto.setPointAmount(new BigDecimal("0"));
        orderDto.setDiscount(new BigDecimal("0"));
        orderDto.setPayAmount(new BigDecimal("0"));
        orderDto.setAmount(new BigDecimal("0"));
        orderDto.setCartIds(cartIds);

        // 预存卡的订单
        if (orderDto.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            orderDto.setCouponId(targetId);
            String orderParam = "";
            BigDecimal totalAmount = new BigDecimal(0);

            MtCoupon couponInfo = couponService.queryCouponById(targetId);
            String inRule = couponInfo.getInRule();
            String[] selectNumArr = selectNum.split(",");
            String[] ruleArr = inRule.split(",");
            for (int i = 0; i < ruleArr.length; i++) {
                String item = ruleArr[i] + "_" + (StringUtils.isNotEmpty(selectNumArr[i]) ? selectNumArr[i] : 0);
                String[] itemArr = item.split("_");
                // 预存金额
                BigDecimal price = new BigDecimal(itemArr[0]);
                // 预存数量
                BigDecimal num = new BigDecimal(selectNumArr[i]);
                BigDecimal amount = price.multiply(num);
                totalAmount = totalAmount.add(amount);
                orderParam = StringUtils.isEmpty(orderParam) ?  item : orderParam + ","+item;
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

        // 升级订单
        if (orderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            orderDto.setParam(targetId+"");
            MtUserGrade userGrade = userGradeService.queryUserGradeById(targetId);
            orderDto.setRemark("付费升级" + userGrade.getName());
            orderDto.setAmount(new BigDecimal(userGrade.getCatchValue().toString()));
        }

        // 商品订单
        if (orderDto.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
            orderDto.setCouponId(couponId);
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
                orderDto.setPointAmount(new BigDecimal(usePoint).divide(new BigDecimal(exchangeNeedPoint)));
                if (orderDto.getPayAmount().compareTo(orderDto.getPointAmount()) > 0) {
                    orderDto.setPayAmount(orderDto.getPayAmount().subtract(orderDto.getPointAmount()));
                } else {
                    orderDto.setPayAmount(new BigDecimal("0"));
                }
            }
        }

        // 会员付款折扣
        if (orderDto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) {
            MtUserGrade userGrade = userGradeService.queryUserGradeById(Integer.parseInt(userInfo.getGradeId()));
            if (userGrade != null) {
                // 是否有会员折扣
                if (userGrade.getDiscount() > 0) {
                    BigDecimal percent = new BigDecimal(userGrade.getDiscount()).divide(new BigDecimal("10"));
                    BigDecimal payAmountDiscount = orderDto.getPayAmount().multiply(percent);
                    orderDto.setDiscount(orderDto.getDiscount().add(orderDto.getPayAmount().subtract(payAmountDiscount)));
                    orderDto.setPayAmount(payAmountDiscount);
                }
            }
        }

        // 生成订单
        MtOrder orderInfo = orderService.saveOrder(orderDto);
        param.put("orderId", orderInfo.getId());

        // 收银台实付金额、优惠金额
        if ((StringUtils.isNotEmpty(cashierPayAmount) || StringUtils.isNotEmpty(cashierDiscountAmount)) && StringUtils.isNotEmpty(operator)) {
            OrderDto reqOrder = new OrderDto();
            reqOrder.setId(orderInfo.getId());
            reqOrder.setAmount(new BigDecimal(cashierPayAmount).add(new BigDecimal(cashierDiscountAmount)));
            reqOrder.setDiscount(new BigDecimal(cashierDiscountAmount));
            orderService.updateOrder(reqOrder);
            orderInfo = orderRepository.findOne(orderInfo.getId());
        }

        // 付款订单使用卡券抵扣
        if (couponId > 0 && orderDto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) {
            if (orderDto.getAmount().compareTo(new BigDecimal("0")) > 0) {
                MtUserCoupon userCouponInfo = userCouponService.getUserCouponDetail(couponId);
                if (userCouponInfo != null) {
                    MtCoupon couponInfo = couponService.queryCouponById(userCouponInfo.getCouponId());
                    if (couponInfo != null) {
                        boolean isEffective = couponService.isCouponEffective(couponInfo);
                        if (isEffective && userCouponInfo.getUserId().equals(orderDto.getUserId())) {
                            // 优惠券，直接减去优惠券金额
                            if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                                String useCode = couponService.useCoupon(couponId, orderDto.getUserId(), orderDto.getStoreId(), orderInfo.getId(), userCouponInfo.getAmount(), "核销");
                                if (StringUtils.isNotEmpty(useCode)) {
                                    orderDto.setCouponId(couponId);
                                    orderDto.setDiscount(orderDto.getDiscount().add(userCouponInfo.getAmount()));
                                    orderDto.setAmount(orderDto.getAmount().subtract(userCouponInfo.getAmount()));
                                }
                            } else if(couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                                // 预存卡，减去余额
                                BigDecimal useCouponAmount = userCouponInfo.getBalance();
                                if (orderDto.getPayAmount().compareTo(userCouponInfo.getBalance()) <= 0) {
                                    useCouponAmount = orderDto.getPayAmount();
                                }
                                String useCode = couponService.useCoupon(couponId, orderDto.getUserId(), orderDto.getStoreId(), orderInfo.getId(), useCouponAmount, "核销");
                                if (StringUtils.isNotEmpty(useCode)) {
                                    orderDto.setCouponId(couponId);
                                    orderDto.setDiscount(orderDto.getDiscount().add(useCouponAmount));
                                    orderDto.setPayAmount(orderDto.getPayAmount().subtract(useCouponAmount));
                                }
                            }
                        }
                    }
                }
            }
        }

        // 生成支付订单
        String ip = CommonUtil.getIPFromHttpRequest(request);
        BigDecimal realPayAmount = orderInfo.getAmount().subtract(new BigDecimal(orderInfo.getDiscount().toString())).subtract(new BigDecimal(orderInfo.getPointAmount().toString()));

        ResponseObject paymentInfo = null;

        // 应付金额大于0才提交微信支付
        if (realPayAmount.compareTo(new BigDecimal("0")) > 0) {
            if (payType.equals("CASH") && StringUtils.isNotEmpty(operator)) {
                // 收银台现金支付，更新为已支付
                OrderDto reqDto = new OrderDto();
                reqDto.setId(orderInfo.getId());
                reqDto.setStatus(OrderStatusEnum.PAID.getKey());
                reqDto.setPayStatus(PayStatusEnum.SUCCESS.getKey());
                reqDto.setPayTime(new Date());
                reqDto.setUpdateTime(new Date());
                orderService.updateOrder(reqDto);
            } else {
                BigDecimal wxPayAmount = realPayAmount.multiply(new BigDecimal("100"));
                // 扫码支付，先返回不处理，后面拿到支付二维码再处理
                if (payType.equals("MICROPAY") && StringUtils.isEmpty(authCode)) {
                    Map<String, String> data = new HashMap<>();
                    paymentInfo = getSuccessResult(data);
                } else {
                    paymentInfo = weixinService.createPrepayOrder(userInfo, orderInfo, (wxPayAmount.intValue()), authCode, 0, ip);
                }
                if (paymentInfo.getData() == null) {
                    return getFailureResult(3000);
                }
            }
        } else {
            // 应付金额是0，直接更新为已支付
            OrderDto reqDto = new OrderDto();
            reqDto.setId(orderInfo.getId());
            reqDto.setStatus(OrderStatusEnum.PAID.getKey());
            reqDto.setPayStatus(PayStatusEnum.SUCCESS.getKey());
            reqDto.setPayAmount(new BigDecimal("0.00"));
            reqDto.setPayTime(new Date());
            reqDto.setUpdateTime(new Date());
            orderService.updateOrder(reqDto);
        }

        Map<String, Object> outParams = new HashMap();
        outParams.put("isCreated", true);
        outParams.put("orderInfo", orderInfo);

        if (paymentInfo != null) {
            outParams.put("payment", paymentInfo.getData());
            outParams.put("payType", "wechat");
        } else {
            outParams.put("payment", null);
            outParams.put("payType", "balance");
        }

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }
}
