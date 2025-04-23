package com.fuint.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.*;
import com.fuint.common.param.OrderListParam;
import com.fuint.common.param.RechargeParam;
import com.fuint.common.param.SettlementParam;
import com.fuint.common.service.*;
import com.fuint.common.util.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
import com.fuint.utils.PropertiesUtil;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weixin.popular.util.JsonUtil;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class OrderServiceImpl extends ServiceImpl<MtOrderMapper, MtOrder> implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private MtOrderMapper mtOrderMapper;

    private MtGoodsMapper mtGoodsMapper;

    private MtOrderGoodsMapper mtOrderGoodsMapper;

    private MtCartMapper mtCartMapper;

    private MtOrderAddressMapper mtOrderAddressMapper;

    private MtConfirmLogMapper mtConfirmLogMapper;

    private MtUserCouponMapper mtUserCouponMapper;

    private MtGoodsSkuMapper mtGoodsSkuMapper;

    private MtRegionMapper mtRegionMapper;

    private MtUserGradeMapper mtUserGradeMapper;

    private MtCouponGoodsMapper mtCouponGoodsMapper;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 会员卡券服务接口
     * */
    private UserCouponService userCouponService;

    /**
     * 收货地址服务接口
     * */
    private AddressService addressService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 积分服务接口
     * */
    private PointService pointService;

    /**
     * 购物车服务接口
     * */
    private CartService cartService;

    /**
     * 商品服务接口
     * */
    private GoodsService goodsService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 会员等级服务接口
     * */
    private UserGradeService userGradeService;

    /**
     * 售后服务接口
     * */
    private RefundService refundService;

    /**
     * 余额服务接口
     * */
    private BalanceService balanceService;

    /**
     * 微信相关服务接口
     * */
    private WeixinService weixinService;

    /**
     * 支付宝服务接口
     * */
    private AlipayService alipayService;

    /**
     * 短信发送服务接口
     * */
    private SendSmsService sendSmsService;

    /**
     * 开卡赠礼服务接口
     * */
    private OpenGiftService openGiftService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 支付服务接口
     * */
    private PaymentService paymentService;

    /**
     * 云打印服务接口
     * */
    private PrinterService printerService;

    /**
     * 获取用户订单列表
     * @param  orderListParam
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaginationResponse getUserOrderList(OrderListParam orderListParam) throws BusinessCheckException {
        Integer pageNumber = orderListParam.getPage() == null ? Constants.PAGE_NUMBER : orderListParam.getPage();
        Integer pageSize = orderListParam.getPageSize() == null ? Constants.PAGE_SIZE : orderListParam.getPageSize();
        String userId = orderListParam.getUserId() == null ? "" : orderListParam.getUserId();
        Integer merchantId = orderListParam.getMerchantId() == null ? 0 : orderListParam.getMerchantId();
        Integer storeId = orderListParam.getStoreId() == null ? 0 : orderListParam.getStoreId();
        String status =  orderListParam.getStatus() == null ? "": orderListParam.getStatus();
        String payStatus =  orderListParam.getPayStatus() == null ? "": orderListParam.getPayStatus();
        String settleStatus =  orderListParam.getSettleStatus() == null ? "": orderListParam.getSettleStatus();
        String dataType =  orderListParam.getDataType() == null ? "": orderListParam.getDataType();
        String type =  orderListParam.getType() == null ? "": orderListParam.getType();
        String orderSn =  orderListParam.getOrderSn() == null ? "": orderListParam.getOrderSn();
        String mobile =  orderListParam.getMobile() == null ? "": orderListParam.getMobile();
        String orderMode =  orderListParam.getOrderMode() == null ? "" : orderListParam.getOrderMode();
        String staffId = orderListParam.getStaffId() == null ? "" : orderListParam.getStaffId();
        String couponId = orderListParam.getCouponId() == null ? "" : orderListParam.getCouponId();
        String storeIds = orderListParam.getStoreIds() == null ? "" : orderListParam.getStoreIds();
        String startTime = orderListParam.getStartTime() == null ? "" : orderListParam.getStartTime();
        String endTime = orderListParam.getEndTime() == null ? "" : orderListParam.getEndTime();
        String keyword = orderListParam.getKeyword() == null ? "" : orderListParam.getKeyword();
        String confirmStatus = orderListParam.getConfirmStatus() == null ? "" : orderListParam.getConfirmStatus();
        List<String> payType = orderListParam.getPayType();

        LambdaQueryWrapper<MtOrder> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtOrder::getStatus, OrderStatusEnum.DELETED.getKey());

        if (dataType.equals("toPay")) {
            status = OrderStatusEnum.CREATED.getKey(); // 待支付
        } else if(dataType.equals("paid")) {
            status = "";
            payStatus = PayStatusEnum.SUCCESS.getKey(); // 已支付
        } else if(dataType.equals("cancel")) {
            status = OrderStatusEnum.CANCEL.getKey();  // 已取消
        } else if(dataType.equals("todo")) {
            // 待核销
            payStatus = PayStatusEnum.SUCCESS.getKey();
            lambdaQueryWrapper.eq(MtOrder::getConfirmStatus, YesOrNoEnum.NO.getKey());
            lambdaQueryWrapper.eq(MtOrder::getType, OrderTypeEnum.GOODS.getKey());
            lambdaQueryWrapper.eq(MtOrder::getOrderMode, OrderModeEnum.ONESELF.getKey());
        } else if(dataType.equals("confirm")) {
            // 已核销
            payStatus = PayStatusEnum.SUCCESS.getKey();
            lambdaQueryWrapper.eq(MtOrder::getType, OrderTypeEnum.GOODS.getKey());
            lambdaQueryWrapper.eq(MtOrder::getConfirmStatus, YesOrNoEnum.YES.getKey());
            lambdaQueryWrapper.eq(MtOrder::getOrderMode, OrderModeEnum.ONESELF.getKey());
        }

        if (StringUtil.isNotEmpty(orderSn)) {
            lambdaQueryWrapper.eq(MtOrder::getOrderSn, orderSn);
        }
        if (StringUtil.isNotEmpty(status)) {
            lambdaQueryWrapper.eq(MtOrder::getStatus, status);
        }
        if (StringUtil.isNotEmpty(payStatus)) {
            lambdaQueryWrapper.eq(MtOrder::getPayStatus, payStatus);
        }
        if (StringUtil.isNotEmpty(settleStatus)) {
            lambdaQueryWrapper.eq(MtOrder::getSettleStatus, settleStatus);
        }
        if (StringUtil.isNotEmpty(keyword)) {
            MtUser userInfo = memberService.queryMemberByMobile(merchantId, keyword);
            if (userInfo != null) {
                lambdaQueryWrapper.and(wq -> wq
                        .like(MtOrder::getOrderSn, keyword)
                        .or()
                        .eq(MtOrder::getUserId, userInfo.getId().toString()));
            } else {
                lambdaQueryWrapper.like(MtOrder::getOrderSn, keyword);
            }
        }
        if (StringUtil.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(merchantId, mobile);
            if (userInfo != null) {
                userId = userInfo.getId().toString();
            } else {
                userId = "0";
            }
        }
        if (StringUtil.isNotEmpty(userId)) {
            lambdaQueryWrapper.eq(MtOrder::getUserId, userId);
        }
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtOrder::getMerchantId, merchantId);
        }
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtOrder::getStoreId, storeId);
        }
        if (StringUtil.isNotEmpty(staffId)) {
            lambdaQueryWrapper.eq(MtOrder::getStaffId, staffId);
        }
        if (StringUtil.isNotEmpty(type)) {
            lambdaQueryWrapper.eq(MtOrder::getType, type);
        }
        if (StringUtil.isNotEmpty(orderMode)) {
            lambdaQueryWrapper.eq(MtOrder::getOrderMode, orderMode);
        }
        if (StringUtils.isNotBlank(couponId)) {
            lambdaQueryWrapper.eq(MtOrder::getCouponId, couponId);
        }
        if (StringUtils.isNotBlank(storeIds)) {
            List<String> idList = Arrays.asList(storeIds.split(","));
            if (idList.size() > 0) {
                lambdaQueryWrapper.in(MtOrder::getStoreId, idList);
            }
        }
        if (StringUtil.isNotEmpty(startTime)) {
            lambdaQueryWrapper.ge(MtOrder::getCreateTime, startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            lambdaQueryWrapper.le(MtOrder::getCreateTime, endTime);
        }
        if (payType != null && payType.size() > 0) {
            lambdaQueryWrapper.in(MtOrder::getPayType, payType);
        }
        if (StringUtil.isNotEmpty(confirmStatus)) {
            lambdaQueryWrapper.eq(MtOrder::getConfirmStatus, confirmStatus);
            lambdaQueryWrapper.eq(MtOrder::getPayStatus, PayStatusEnum.SUCCESS.getKey());
            lambdaQueryWrapper.eq(MtOrder::getType, OrderTypeEnum.GOODS.getKey());
        }
        lambdaQueryWrapper.orderByDesc(MtOrder::getId);
        Page<MtOrder> pageHelper = PageHelper.startPage(pageNumber, pageSize);
        List<MtOrder> orderList = mtOrderMapper.selectList(lambdaQueryWrapper);

        List<UserOrderDto> dataList = new ArrayList<>();
        if (orderList.size() > 0) {
            for (MtOrder order : orderList) {
                 UserOrderDto dto = getOrderDetail(order,false, false);
                 dataList.add(dto);
            }
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<UserOrderDto> paginationResponse = new PaginationResponse(pageImpl, UserOrderDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 保存订单信息
     *
     * @param  orderDto 订单参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "提交订单信息")
    public MtOrder saveOrder(OrderDto orderDto) throws BusinessCheckException {
        logger.info("orderService.saveOrder orderDto = {}", JsonUtil.toJSONString(orderDto));
        MtOrder mtOrder;
        if (null != orderDto.getId() && orderDto.getId() > 0) {
            mtOrder = mtOrderMapper.selectById(orderDto.getId());
        } else {
            mtOrder = new MtOrder();
        }

        // 检查店铺是否已被禁用
        if (orderDto.getStoreId() != null && orderDto.getStoreId() > 0) {
            MtStore storeInfo = storeService.queryStoreById(orderDto.getStoreId());
            if (storeInfo != null) {
                if (!storeInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    orderDto.setStoreId(0);
                }
            }
        }

        String orderSn;
        if (orderDto.getId() == null || orderDto.getId() < 1) {
            orderSn = CommonUtil.createOrderSN(orderDto.getUserId() + "");
            mtOrder.setOrderSn(orderSn);
        } else {
            orderSn = mtOrder.getOrderSn();
        }

        mtOrder.setUserId(orderDto.getUserId());
        mtOrder.setMerchantId(orderDto.getMerchantId());
        mtOrder.setStoreId(orderDto.getStoreId());
        mtOrder.setCouponId(orderDto.getCouponId());
        mtOrder.setParam(orderDto.getParam());
        mtOrder.setRemark(orderDto.getRemark());
        mtOrder.setStatus(OrderStatusEnum.CREATED.getKey());
        mtOrder.setType(orderDto.getType());
        mtOrder.setAmount(orderDto.getAmount());
        mtOrder.setPayAmount(orderDto.getPayAmount());
        mtOrder.setDiscount(orderDto.getDiscount());
        mtOrder.setPayStatus(PayStatusEnum.WAIT.getKey());
        mtOrder.setPlatform(orderDto.getPlatform());
        mtOrder.setPointAmount(orderDto.getPointAmount());
        mtOrder.setUsePoint(orderDto.getUsePoint());
        mtOrder.setOrderMode(orderDto.getOrderMode());
        mtOrder.setPayType(orderDto.getPayType());
        mtOrder.setOperator(orderDto.getOperator());
        mtOrder.setStaffId(orderDto.getStaffId());
        mtOrder.setIsVisitor(orderDto.getIsVisitor());
        mtOrder.setUpdateTime(new Date());
        mtOrder.setDeliveryFee(orderDto.getDeliveryFee() == null ? new BigDecimal(0) : orderDto.getDeliveryFee());
        mtOrder.setSettleStatus(SettleStatusEnum.WAIT.getKey());
        mtOrder.setConfirmStatus(YesOrNoEnum.NO.getKey());

        if (mtOrder.getId() == null || mtOrder.getId() <= 0) {
            mtOrder.setCreateTime(new Date());
        }
        // 核销码
        if (orderDto.getPlatform() == null) {
            orderDto.setPlatform("");
        }
        if (mtOrder.getVerifyCode() == null && !orderDto.getPlatform().equals(PlatformTypeEnum.PC.getCode())) {
            mtOrder.setVerifyCode(SeqUtil.getRandomNumber(6));
        }

        // 首先生成订单
        mtOrderMapper.insert(mtOrder);
        MtOrder orderInfo = mtOrderMapper.selectById(mtOrder.getId());
        mtOrder.setId(orderInfo.getId());

        // 会员相关信息
        MtUser userInfo = memberService.queryMemberById(orderDto.getUserId());
        MtUserGrade userGrade = userGradeService.queryUserGradeById(orderDto.getMerchantId(), userInfo.getGradeId() != null ? Integer.parseInt(userInfo.getGradeId()) : 1, orderDto.getUserId());
        BigDecimal percent = new BigDecimal("0");
        if (userGrade != null && userGrade.getDiscount() != null && userGrade.getDiscount() > 0 && !userInfo.getIsStaff().equals(YesOrNoEnum.YES.getKey())) {
            // 会员折扣
            percent = new BigDecimal(userGrade.getDiscount()).divide(new BigDecimal("10"), BigDecimal.ROUND_CEILING, 4);
            if (percent.compareTo(new BigDecimal("0")) <= 0) {
                percent = new BigDecimal("1");
            }
        }

        // 如果没有指定店铺，则读取默认的店铺
        if (orderDto.getStoreId() == null || orderDto.getStoreId() <= 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("status", StatusEnum.ENABLED.getKey());
            params.put("is_default", YesOrNoEnum.YES.getKey());
            List<MtStore> storeList = storeService.queryStoresByParams(params);
            if (storeList.size() > 0) {
                mtOrder.setStoreId(storeList.get(0).getId());
            } else {
                mtOrder.setStoreId(0);
            }
        }

        mtOrder.setUpdateTime(new Date());
        if (mtOrder.getCreateTime() == null) {
            mtOrder.setCreateTime(new Date());
        }

        // 计算商品订单总金额
        List<MtCart> cartList = new ArrayList<>();
        Map<String, Object> cartData = new HashMap<>();
        if (orderDto.getType().equals(OrderTypeEnum.GOODS.getKey())) {
            if (StringUtil.isNotEmpty(orderDto.getCartIds())) {
                Map<String, Object> param = new HashMap<>();
                param.put("status", StatusEnum.ENABLED.getKey());
                param.put("ids", orderDto.getCartIds());
                cartList = cartService.queryCartListByParams(param);
                if (cartList.size() < 1) {
                    throw new BusinessCheckException("生成订单失败，请稍后重试");
                }
            } else {
                if (orderDto.getGoodsId() == null || orderDto.getGoodsId() <= 0) {
                    throw new BusinessCheckException("生成订单失败，请稍后重试");
                }
                // 直接购买
                MtCart mtCart = new MtCart();
                mtCart.setGoodsId(orderDto.getGoodsId());
                mtCart.setSkuId(orderDto.getSkuId());
                mtCart.setNum(orderDto.getBuyNum());
                mtCart.setId(0);
                mtCart.setUserId(orderDto.getUserId());
                mtCart.setStatus(StatusEnum.ENABLED.getKey());
                cartList.add(mtCart);
            }

            boolean isUsePoint = orderDto.getUsePoint() > 0 ? true : false;
            cartData = calculateCartGoods(orderInfo.getMerchantId(), orderDto.getUserId(), cartList, orderDto.getCouponId(), isUsePoint, orderDto.getPlatform(), orderInfo.getOrderMode());

            mtOrder.setAmount(new BigDecimal(cartData.get("totalPrice").toString()));
            mtOrder.setUsePoint(Integer.parseInt(cartData.get("usePoint").toString()));
            mtOrder.setDiscount(new BigDecimal(cartData.get("couponAmount").toString()));

            // 实付金额
            BigDecimal payAmount = mtOrder.getAmount().subtract(mtOrder.getPointAmount()).subtract(mtOrder.getDiscount());
            if (payAmount.compareTo(new BigDecimal("0")) > 0) {
                mtOrder.setPayAmount(payAmount);
            } else {
                mtOrder.setPayAmount(new BigDecimal("0"));
            }

            // 购物使用了卡券
            if (mtOrder.getCouponId() > 0) {
                // 查询是否适用商品
                MtUserCoupon userCoupon = mtUserCouponMapper.selectById(mtOrder.getCouponId());
                if (userCoupon != null) {
                    MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());
                    if (couponInfo.getApplyGoods() != null && couponInfo.getApplyGoods().equals(ApplyGoodsEnum.PARK_GOODS.getKey())) {
                        List<MtCouponGoods> couponGoodsList = mtCouponGoodsMapper.getCouponGoods(couponInfo.getId());
                        if (couponGoodsList != null && couponGoodsList.size() > 0 && cartList.size() > 0) {
                            List<Integer> applyGoodsIds = new ArrayList<>();
                            List<Integer> goodsIds = new ArrayList<>();
                            for (MtCouponGoods mtCouponGoods : couponGoodsList) {
                                 applyGoodsIds.add(mtCouponGoods.getGoodsId());
                            }
                            for (MtCart mtCart : cartList) {
                                 goodsIds.add(mtCart.getGoodsId());
                            }
                            List<Integer> intersection = applyGoodsIds.stream()
                                    .filter(goodsIds::contains)
                                    .collect(Collectors.toList());
                            if (intersection.size() == 0) {
                                throw new BusinessCheckException("该卡券不适用于购买的商品列表");
                            }
                        }
                    }
                }
                updateOrder(mtOrder);
                String useCode = couponService.useCoupon(mtOrder.getCouponId(), mtOrder.getUserId(), mtOrder.getStoreId(), mtOrder.getId(), mtOrder.getDiscount(), "购物使用卡券");
                // 卡券使用失败
                if (StringUtil.isEmpty(useCode)) {
                    mtOrder.setDiscount(new BigDecimal("0"));
                    mtOrder.setCouponId(0);
                }
            }
        }

        // 会员付款类订单
        if (orderDto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) {
            if (userInfo != null && userInfo.getGradeId() != null && orderDto.getIsVisitor().equals(YesOrNoEnum.NO.getKey())) {
                if (percent.compareTo(new BigDecimal("0")) > 0 && !userInfo.getIsStaff().equals(YesOrNoEnum.YES.getKey())) {
                    // 会员折扣
                    BigDecimal payAmountDiscount = mtOrder.getAmount().multiply(percent);
                    if (payAmountDiscount.compareTo(new BigDecimal("0")) > 0) {
                        mtOrder.setDiscount(mtOrder.getDiscount().add(mtOrder.getAmount().subtract(payAmountDiscount)));
                        mtOrder.setPayAmount(payAmountDiscount);
                    } else {
                        mtOrder.setPayAmount(new BigDecimal("0"));
                    }
                }
            }
        }

        // 再次更新订单
        try {
             orderInfo = updateOrder(mtOrder);
        } catch (Exception e) {
             logger.error("OrderService 生成订单失败...");
             throw new BusinessCheckException("生成订单失败，请稍后重试");
        }

        // 扣减积分
        if (orderDto.getUsePoint() > 0) {
            try {
                MtPoint reqPointDto = new MtPoint();
                reqPointDto.setUserId(orderDto.getUserId());
                reqPointDto.setAmount(-orderDto.getUsePoint());
                reqPointDto.setOrderSn(orderSn);
                reqPointDto.setDescription("支付扣除" + orderDto.getUsePoint() + "积分");
                reqPointDto.setOperator("");
                pointService.addPoint(reqPointDto);
            } catch (BusinessCheckException e) {
                logger.error("OrderService 扣减积分失败...{}", e.getMessage());
                throw new BusinessCheckException("扣减积分失败，请稍后重试");
            }
        }

        // 如果是商品订单，生成订单商品
        if (orderDto.getType().equals(OrderTypeEnum.GOODS.getKey()) && cartList.size() > 0) {
            Object listObject = cartData.get("list");
            List<ResCartDto> lists =(ArrayList<ResCartDto>)listObject;
            BigDecimal memberDiscount = new BigDecimal("0");
            for (ResCartDto cart : lists) {
                 MtOrderGoods orderGoods = new MtOrderGoods();
                 orderGoods.setOrderId(orderInfo.getId());
                 orderGoods.setGoodsId(cart.getGoodsId());
                 orderGoods.setSkuId(cart.getSkuId());
                 orderGoods.setNum(cart.getNum());
                 // 计算会员折扣
                 BigDecimal price = cart.getGoodsInfo().getPrice();
                 boolean isDiscount = cart.getGoodsInfo().getIsMemberDiscount().equals(YesOrNoEnum.YES.getKey()) ? true : false;
                 if (percent.compareTo(new BigDecimal("0")) > 0 && isDiscount) {
                     orderGoods.setPrice(price.multiply(percent));
                     BigDecimal discount = price.subtract(price.multiply(percent)).multiply(new BigDecimal(cart.getNum()));
                     orderGoods.setDiscount(discount);
                     memberDiscount = memberDiscount.add(discount);
                 } else {
                     orderGoods.setPrice(price);
                     orderGoods.setDiscount(new BigDecimal("0"));
                 }
                 orderGoods.setStatus(StatusEnum.ENABLED.getKey());
                 orderGoods.setCreateTime(new Date());
                 orderGoods.setUpdateTime(new Date());
                 mtOrderGoodsMapper.insert(orderGoods);
                 // 扣减库存
                 MtGoods goodsInfo = mtGoodsMapper.selectById(cart.getGoodsId());
                 if (goodsInfo.getIsSingleSpec().equals(YesOrNoEnum.YES.getKey())) {
                     // 单规格减去库存
                     Integer stock = goodsInfo.getStock() - cart.getNum();
                     if (stock < 0) {
                         throw new BusinessCheckException("商品“" + goodsInfo.getName() + "”库存不足，订单提交失败");
                     }
                     goodsInfo.setStock(stock);
                     mtGoodsMapper.updateById(goodsInfo);
                 } else {
                     // 多规格减去库存
                     MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(cart.getSkuId());
                     if (mtGoodsSku != null) {
                         Integer stock = mtGoodsSku.getStock() - cart.getNum();
                         if (stock < 0) {
                             throw new BusinessCheckException("商品sku编码“" + mtGoodsSku.getSkuNo() +"”库存不足，订单提交失败");
                         }
                         mtGoodsSku.setStock(stock);
                         mtGoodsSkuMapper.updateById(mtGoodsSku);

                         if (goodsInfo.getStock() != null && goodsInfo.getStock() > 0) {
                             Integer goodsStock = goodsInfo.getStock() - cart.getNum();
                             if (goodsStock >= 0) {
                                 goodsInfo.setStock(goodsStock);
                                 mtGoodsMapper.updateById(goodsInfo);
                             }
                         }
                     }
                 }
                 if (cart.getId() > 0) {
                     mtCartMapper.deleteById(cart.getId());
                 }
            }

            // 会员折扣
            if (memberDiscount.compareTo(new BigDecimal("0")) > 0 && !userInfo.getIsStaff().equals(YesOrNoEnum.YES.getKey())) {
                orderInfo.setDiscount(orderInfo.getDiscount().add(memberDiscount));
                if (orderInfo.getPayAmount().subtract(memberDiscount).compareTo(new BigDecimal("0")) > 0) {
                    orderInfo.setPayAmount(orderInfo.getPayAmount().subtract(memberDiscount));
                } else {
                    orderInfo.setPayAmount(new BigDecimal("0"));
                }
                orderInfo.setUpdateTime(new Date());
                orderInfo = updateOrder(orderInfo);
            }

            // 需要配送的订单，生成配送地址
            if (orderDto.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey())) {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", orderDto.getUserId().toString());
                params.put("isDefault", YesOrNoEnum.YES.getKey());
                List<MtAddress> addressList = addressService.queryListByParams(params);
                MtAddress mtAddress;
                if (addressList.size() > 0) {
                    mtAddress = addressList.get(0);
                } else {
                    throw new BusinessCheckException("配送地址出错了，请重新选择配送地址");
                }
                MtOrderAddress orderAddress = new MtOrderAddress();
                orderAddress.setOrderId(orderInfo.getId());
                orderAddress.setUserId(orderDto.getUserId());
                orderAddress.setName(mtAddress.getName());
                orderAddress.setMobile(mtAddress.getMobile());
                orderAddress.setCityId(mtAddress.getCityId());
                orderAddress.setProvinceId(mtAddress.getProvinceId());
                orderAddress.setRegionId(mtAddress.getRegionId());
                orderAddress.setDetail(mtAddress.getDetail());
                orderAddress.setCreateTime(new Date());
                mtOrderAddressMapper.insert(orderAddress);
            }
        }

        return orderInfo;
    }

    /**
     * 订单结算
     * @param request
     * @param param 结算参数
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> doSettle(HttpServletRequest request, SettlementParam param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String isWechat = request.getHeader("isWechat") == null ? YesOrNoEnum.NO.getKey() : request.getHeader("isWechat");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String ip = CommonUtil.getIPFromHttpRequest(request);
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
        String orderMode = StringUtil.isEmpty(param.getOrderMode()) ? OrderModeEnum.ONESELF.getKey() : param.getOrderMode(); // 订单模式(配送or自取)
        Integer orderId = param.getOrderId() == null ? null : param.getOrderId(); // 订单ID
        Integer merchantId = merchantService.getMerchantId(merchantNo);
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

            if (param.getStaffId() != null && param.getStaffId() > 0) {
                staffId = param.getStaffId();
            }

            storeId = accountInfo.getStoreId();
            merchantId = accountInfo.getMerchantId();
            if (storeId <= 0) {
                MtMerchant mtMerchant = merchantService.queryMerchantById(merchantId);
                if (mtMerchant != null) {
                    MtStore mtStore = storeService.getDefaultStore(mtMerchant.getNo());
                    if (mtStore != null) {
                        storeId = mtStore.getId();
                    }
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
            if (mtStaff == null) {
                mtStaff = staffService.queryStaffByMobile(userInfo.getMobile());
            }
            if (mtStaff != null) {
                operator = mtStaff.getRealName();
            }
        }

        MtSetting config = settingService.querySettingByName(merchantId, storeId, SettingTypeEnum.ORDER.getKey(), OrderSettingEnum.IS_CLOSE.getKey());
        if (config != null && config.getValue().equals(YesOrNoEnum.TRUE.getKey())) {
            throw new BusinessCheckException("系统已关闭交易功能，请稍后再试！");
        }

        // 收银台通过手机号自动注册会员信息
        if ((userInfo == null || StringUtil.isEmpty(token))) {
            String mobile = param.getMobile() == null ? "" : param.getMobile();
            if (StringUtil.isNotEmpty(operator) && StringUtil.isNotEmpty(mobile)) {
                userInfo = memberService.queryMemberByMobile(merchantId, mobile);
                // 自动注册会员
                if (userInfo == null) {
                    userInfo = memberService.addMemberByMobile(merchantId, mobile, "0", ip);
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
        orderDto.setMerchantId(merchantId);
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

        MtSetting pointSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.POINT.getKey(), PointSettingEnum.CAN_USE_AS_MONEY.getKey());
        // 使用积分数量
        if (pointSetting != null && pointSetting.getValue().equals(YesOrNoEnum.TRUE.getKey())) {
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
            orderDto.setDeliveryFee(new BigDecimal("0"));
        }

        // 会员升级订单
        if (orderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            orderDto.setParam(targetId.toString());
            orderDto.setCouponId(couponId);
            MtUserGrade userGrade = userGradeService.queryUserGradeById(merchantId, targetId, orderDto.getUserId());
            if (userGrade != null) {
                orderDto.setRemark("付费升级" + userGrade.getName());
                orderDto.setAmount(new BigDecimal(userGrade.getCatchValue().toString()));
            }
        }

        // 商品订单
        if (orderDto.getType().equals(OrderTypeEnum.GOODS.getKey())) {
            orderDto.setCouponId(couponId);
        }

        // 商品订单且配送要加上配送费用
        if (orderDto.getType().equals(OrderTypeEnum.GOODS.getKey()) && orderDto.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey())) {
            MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.ORDER.getKey(), OrderSettingEnum.DELIVERY_FEE.getKey());
            if (mtSetting != null && StringUtil.isNotEmpty(mtSetting.getValue())) {
                BigDecimal deliveryFee = new BigDecimal(mtSetting.getValue());
                if (deliveryFee.compareTo(new BigDecimal("0")) > 0) {
                    orderDto.setDeliveryFee(deliveryFee);
                }
            }
        }

        // 使用积分抵扣
        if (usePoint > 0) {
            List<MtSetting> settingList = settingService.getSettingList(merchantId, SettingTypeEnum.POINT.getKey());
            String canUsedAsMoney = YesOrNoEnum.FALSE.getKey();
            String exchangeNeedPoint = "0";
            for (MtSetting setting : settingList) {
                if (setting.getName().equals("canUsedAsMoney")) {
                    canUsedAsMoney = setting.getValue();
                } else if (setting.getName().equals("exchangeNeedPoint")) {
                    exchangeNeedPoint = setting.getValue();
                }
            }
            // 是否可以使用积分，并且积分数量足够
            if (canUsedAsMoney.equals(YesOrNoEnum.TRUE.getKey()) && Float.parseFloat(exchangeNeedPoint) > 0 && (userInfo.getPoint() >= usePoint)) {
                orderDto.setUsePoint(usePoint);
                orderDto.setPointAmount(new BigDecimal(usePoint).divide(new BigDecimal(exchangeNeedPoint), BigDecimal.ROUND_CEILING, 4));
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
            orderInfo = saveOrder(orderDto);
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
            updateOrder(reqOrder);
            orderInfo = getOrderInfo(orderInfo.getId());
        }

        // 订单中使用卡券抵扣(付款订单、会员升级订单)
        if (couponId > 0 && (orderDto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) || orderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            if (orderDto.getAmount().compareTo(new BigDecimal("0")) > 0) {
                MtUserCoupon userCouponInfo = userCouponService.getUserCouponDetail(couponId);
                if (userCouponInfo != null) {
                    MtCoupon couponInfo = couponService.queryCouponById(userCouponInfo.getCouponId());
                    if (couponInfo != null) {
                        boolean isEffective = couponService.isCouponEffective(couponInfo, userCouponInfo);
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
                                        updateOrder(orderDto);
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
                                        updateOrder(orderDto);
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
        orderInfo = getOrderInfo(orderInfo.getId());
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

        MtSetting delivery = settingService.querySettingByName(merchantId, SettingTypeEnum.ORDER.getKey(),OrderSettingEnum.DELIVERY_MIN_AMOUNT.getKey());
        if (delivery != null && orderInfo.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey())) {
            BigDecimal deliveryMinAmount = new BigDecimal(delivery.getValue());
            if (deliveryMinAmount.compareTo(new BigDecimal("0")) > 0 && deliveryMinAmount.compareTo(orderInfo.getAmount()) > 0) {
                throw new BusinessCheckException("订单起送金额：" + deliveryMinAmount + "元");
            }
        }

        ResponseObject paymentInfo = null;
        String errorMessage = "";

        // 应付金额大于0才提交微信支付
        if (realPayAmount.compareTo(new BigDecimal("0")) > 0) {
            if (payType.equals(PayTypeEnum.CASH.getKey()) && StringUtil.isNotEmpty(operator)) {
                // 收银台现金支付，更新为已支付
                setOrderPayed(orderInfo.getId(), null);
            } else  if (payType.equals(PayTypeEnum.STORE.getKey())) {
                // 门店支付，不做任何操作
            } else if(payType.equals(PayTypeEnum.BALANCE.getKey())) {
                // 余额支付
                MtBalance balance = new MtBalance();
                balance.setMobile(userInfo.getMobile());
                balance.setOrderSn(orderInfo.getOrderSn());
                balance.setUserId(userInfo.getId());
                balance.setMerchantId(userInfo.getMerchantId());
                BigDecimal balanceAmount = realPayAmount.subtract(realPayAmount).subtract(realPayAmount);
                balance.setAmount(balanceAmount);
                boolean isPay = balanceService.addBalance(balance, true);
                if (isPay) {
                    setOrderPayed(orderInfo.getId(), realPayAmount);
                } else {
                    errorMessage = PropertiesUtil.getResponseErrorMessageByCode(5001);
                }
            } else {
                BigDecimal wxPayAmount = realPayAmount.multiply(new BigDecimal("100"));
                // 扫码支付，先返回不处理，后面拿到支付二维码再处理
                if ((payType.equals(PayTypeEnum.MICROPAY.getKey()) || payType.equals(PayTypeEnum.ALISCAN.getKey())) && StringUtil.isEmpty(authCode)) {
                    paymentInfo = new ResponseObject(200, "请求成功", new HashMap<>());
                } else {
                    paymentInfo = paymentService.createPrepayOrder(userInfo, orderInfo, (wxPayAmount.intValue()), authCode, 0, ip, platform, isWechat);
                }
                if (paymentInfo.getData() == null) {
                    errorMessage = StringUtil.isNotEmpty(paymentInfo.getMessage()) ? paymentInfo.getMessage() : PropertiesUtil.getResponseErrorMessageByCode(3000);
                }
            }
        } else {
            // 应付金额是0，直接更新为已支付
            setOrderPayed(orderInfo.getId(), null);
        }

        orderInfo = getOrderInfo(orderInfo.getId());
        Map<String, Object> outParams = new HashMap();
        outParams.put("isCreated", true);
        outParams.put("orderInfo", orderInfo);
        outParams.put("payType", payType);

        if (paymentInfo != null) {
            outParams.put("payment", paymentInfo.getData());
        } else {
            outParams.put("payment", null);
        }

        // 1分钟后发送小程序订阅消息
        Date nowTime = new Date();
        Date sendTime = new Date(nowTime.getTime() + 60000);
        Map<String, Object> params = new HashMap<>();
        String dateTime = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm");
        params.put("time", dateTime);
        params.put("orderSn", orderInfo.getOrderSn());
        params.put("remark", "您的订单已生成，请留意~");
        weixinService.sendSubscribeMessage(merchantId, userInfo.getId(), userInfo.getOpenId(), WxMessageEnum.ORDER_CREATED.getKey(), "pages/order/index", params, sendTime);

        if (StringUtil.isNotEmpty(errorMessage)) {
            throw new BusinessCheckException(errorMessage);
        } else {
            return outParams;
        }
    }

    /**
     * 获取订单详情
     *
     * @param  orderId 订单ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtOrder getOrderInfo(Integer orderId) {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        return mtOrderMapper.selectById(orderId);
    }

    /**
     * 根据ID获取订单详情
     *
     * @param orderId 订单ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public UserOrderDto getOrderById(Integer orderId) throws BusinessCheckException {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        MtOrder mtOrder = mtOrderMapper.selectById(orderId);
        return getOrderDetail(mtOrder, true, true);
    }

    /**
     * 根据ID获取我的订单详情
     *
     * @param  orderId 订单ID
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public UserOrderDto getMyOrderById(Integer orderId) throws BusinessCheckException {
        if (orderId == null || orderId <= 0) {
            return null;
        }
        MtOrder mtOrder = mtOrderMapper.selectById(orderId);
        UserOrderDto orderInfo = getOrderDetail(mtOrder, true, true);

        // 售后订单
        MtRefund refund = refundService.getRefundByOrderId(orderId);
        orderInfo.setRefundInfo(refund);

        orderInfo.setVerifyCode(mtOrder.getVerifyCode());
        return orderInfo;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param remark 取消备注
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "取消订单")
    public MtOrder cancelOrder(Integer orderId, String remark) throws BusinessCheckException {
        MtOrder mtOrder = mtOrderMapper.selectById(orderId);
        logger.info("orderService.cancelOrder orderId = {}, remark = {}", orderId, remark);

        if (mtOrder != null && mtOrder.getStatus().equals(OrderStatusEnum.CREATED.getKey()) && mtOrder.getPayStatus().equals(PayStatusEnum.WAIT.getKey())) {
            if (StringUtil.isNotEmpty(remark)) {
                mtOrder.setRemark(remark);
            }

            mtOrder.setStatus(OrderStatusEnum.CANCEL.getKey());
            mtOrderMapper.updateById(mtOrder);

            // 返还积分
            if (mtOrder.getPointAmount() != null && mtOrder.getUsePoint() > 0) {
                MtPoint reqPointDto = new MtPoint();
                reqPointDto.setUserId(mtOrder.getUserId());
                reqPointDto.setAmount(mtOrder.getUsePoint());
                reqPointDto.setDescription("订单取消" + mtOrder.getOrderSn() + "退回"+ mtOrder.getUsePoint() +"积分");
                reqPointDto.setOrderSn(mtOrder.getOrderSn());
                reqPointDto.setOperator("");
                pointService.addPoint(reqPointDto);
            }

            // 返还卡券
            List<MtConfirmLog> confirmLogList = mtConfirmLogMapper.getOrderConfirmLogList(mtOrder.getId());
            if (confirmLogList.size() > 0) {
                for (MtConfirmLog log : confirmLogList) {
                    MtCoupon couponInfo = couponService.queryCouponById(log.getCouponId());
                    MtUserCoupon userCouponInfo = mtUserCouponMapper.selectById(log.getUserCouponId());

                    if (userCouponInfo != null) {
                        // 优惠券直接置为未使用
                        if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                            userCouponInfo.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                            mtUserCouponMapper.updateById(userCouponInfo);
                        }

                        // 储值卡把余额加回去
                        if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                            BigDecimal balance = userCouponInfo.getBalance();
                            BigDecimal newBalance = balance.add(log.getAmount());
                            if (newBalance.compareTo(userCouponInfo.getAmount()) <= 0) {
                                userCouponInfo.setBalance(newBalance);
                                userCouponInfo.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                            }
                            mtUserCouponMapper.updateById(userCouponInfo);
                        }

                        // 撤销核销记录
                        log.setStatus(StatusEnum.DISABLE.getKey());
                        mtConfirmLogMapper.updateById(log);
                    }
                }
            }

            // 返还库存
            Map<String, Object> params = new HashMap<>();
            params.put("ORDER_ID", mtOrder.getId());
            List<MtOrderGoods> orderGoodsList = mtOrderGoodsMapper.selectByMap(params);
            if (orderGoodsList != null && orderGoodsList.size() > 0) {
                for (MtOrderGoods mtOrderGoods : orderGoodsList) {
                     MtGoods mtGoods = mtGoodsMapper.selectById(mtOrderGoods.getGoodsId());
                     // 商品已不存在
                     if (mtGoods == null) {
                         continue;
                     }
                     mtGoods.setStock(mtOrderGoods.getNum() + mtGoods.getStock());
                     mtGoodsMapper.updateById(mtGoods);
                     if (mtOrderGoods.getSkuId() != null && mtOrderGoods.getSkuId() > 0) {
                         MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(mtOrderGoods.getSkuId());
                         if (mtGoodsSku != null && mtGoodsSku.getStock() != null && mtOrderGoods.getNum() != null) {
                             mtGoodsSku.setStock(mtGoodsSku.getStock() + mtOrderGoods.getNum());
                             mtGoodsSkuMapper.updateById(mtGoodsSku);
                         }
                     }
                }
            }
        }

        return mtOrder;
    }

    /**
     * 根据订单ID删除
     *
     * @param orderId 订单ID
     * @param operator 操作人
     * @return
     */
    @Override
    @OperationServiceLog(description = "删除订单信息")
    public void deleteOrder(Integer orderId, String operator) {
        logger.info("orderService.deleteOrder orderId = {}, operator = {}", orderId, operator);
        MtOrder mtOrder = mtOrderMapper.selectById(orderId);
        if (mtOrder == null) {
            return;
        }

        mtOrder.setStatus(OrderStatusEnum.DELETED.getKey());
        mtOrder.setUpdateTime(new Date());
        mtOrder.setOperator(operator);

        mtOrderMapper.updateById(mtOrder);
    }

    /**
     * 根据订单号获取订单详情
     *
     * @param  orderSn 订单号
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public UserOrderDto getOrderByOrderSn(String orderSn) throws BusinessCheckException {
        MtOrder orderInfo = mtOrderMapper.findByOrderSn(orderSn);
        if (orderInfo == null) {
            return null;
        }
        return getOrderDetail(orderInfo, true, true);
    }

    /**
     * 根据订单号获取订单信息
     *
     * @param  orderSn 订单号
     * @return
     */
    @Override
    public MtOrder getOrderInfoByOrderSn(String orderSn) {
        return mtOrderMapper.findByOrderSn(orderSn);
    }

    /**
     * 更新订单
     *
     * @param  orderDto 订单参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新订单信息")
    public MtOrder updateOrder(OrderDto orderDto) throws BusinessCheckException {
        logger.info("orderService.updateOrder orderDto = {}", JsonUtil.toJSONString(orderDto));
        MtOrder mtOrder = mtOrderMapper.selectById(orderDto.getId());
        if (null == mtOrder || OrderStatusEnum.DELETED.getKey().equals(mtOrder.getStatus())) {
            throw new BusinessCheckException("该订单状态异常");
        }

        mtOrder.setId(orderDto.getId());
        mtOrder.setUpdateTime(new Date());

        if (null != orderDto.getOperator()) {
            mtOrder.setOperator(orderDto.getOperator());
        }

        if (null != orderDto.getStatus()) {
            if (orderDto.getStatus().equals(OrderStatusEnum.CANCEL.getKey()) || orderDto.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
                orderDto.setPayStatus(PayStatusEnum.WAIT.getKey());
            }
            if (orderDto.getStatus().equals(OrderStatusEnum.CANCEL.getKey())) {
                cancelOrder(orderDto.getId(), "取消订单");
            } else {
                mtOrder.setStatus(orderDto.getStatus());
            }
            if (orderDto.getStatus().equals(OrderStatusEnum.PAID.getKey())) {
                mtOrder.setPayStatus(PayStatusEnum.SUCCESS.getKey());
                mtOrder.setPayTime(new Date());
            }
        }

        if (null != orderDto.getPayAmount()) {
            mtOrder.setPayAmount(orderDto.getPayAmount());
        }

        if (null != orderDto.getAmount()) {
            mtOrder.setAmount(orderDto.getAmount());
        }

        if (null != orderDto.getVerifyCode() && StringUtil.isNotEmpty(orderDto.getVerifyCode())) {
            if (orderDto.getVerifyCode().equals(mtOrder.getVerifyCode()) || StringUtil.isEmpty(mtOrder.getVerifyCode())) {
                mtOrder.setStatus(OrderStatusEnum.DELIVERED.getKey());
                mtOrder.setVerifyCode("");
                mtOrder.setConfirmStatus(YesOrNoEnum.YES.getKey());
                mtOrder.setConfirmTime(new Date());
                mtOrder.setConfirmRemark(orderDto.getConfirmRemark());
            } else {
                throw new BusinessCheckException("核销码错误，请确认！");
            }
        }

        if (null != orderDto.getDiscount()) {
            mtOrder.setDiscount(orderDto.getDiscount());
        }

        if (null != orderDto.getPayTime()) {
            mtOrder.setPayTime(orderDto.getPayTime());
        }

        if (null != orderDto.getPayType()) {
            mtOrder.setPayType(orderDto.getPayType());
        }

        if (null != orderDto.getPayStatus()) {
            mtOrder.setPayStatus(orderDto.getPayStatus());
        }

        if (null != orderDto.getExpressInfo()) {
            mtOrder.setExpressInfo(JSONObject.toJSONString(orderDto.getExpressInfo()));
        }

        if (null != orderDto.getOrderMode()) {
            mtOrder.setOrderMode(orderDto.getOrderMode());
        }

        if (null != orderDto.getRemark()) {
            mtOrder.setRemark(orderDto.getRemark());
        }

        mtOrderMapper.updateById(mtOrder);
        return mtOrder;
    }

    /**
     * 更新订单
     *
     * @param mtOrder 订单信息
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MtOrder updateOrder(MtOrder mtOrder) {
        mtOrder.setUpdateTime(new Date());
        Integer id = mtOrderMapper.updateById(mtOrder);
        if (id > 0) {
            mtOrder = mtOrderMapper.selectById(mtOrder.getId());
        }
        return mtOrder;
    }

    /**
     * 把订单置为已支付
     *
     * @param orderId 订单ID
     * @param payAmount 支付金额
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改订单为已支付")
    public Boolean setOrderPayed(Integer orderId, BigDecimal payAmount) throws BusinessCheckException {
        MtOrder mtOrder = mtOrderMapper.selectById(orderId);
        if (mtOrder == null) {
            return false;
        }
        if (mtOrder.getPayStatus().equals(PayStatusEnum.SUCCESS.getKey())) {
            return true;
        }
        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderId);
        reqDto.setStatus(OrderStatusEnum.PAID.getKey());
        reqDto.setPayStatus(PayStatusEnum.SUCCESS.getKey());
        if (payAmount != null) {
            reqDto.setPayAmount(payAmount);
        }
        reqDto.setPayTime(new Date());
        reqDto.setUpdateTime(new Date());
        updateOrder(reqDto);

        // 处理会员升级订单
        if (mtOrder.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            openGiftService.openGift(mtOrder.getUserId(), Integer.parseInt(mtOrder.getParam()), false);
        }

        // 处理购物订单
        UserOrderDto orderInfo = getOrderByOrderSn(mtOrder.getOrderSn());
        if (orderInfo.getType().equals(OrderTypeEnum.GOODS.getKey())) {
            try {
                List<OrderGoodsDto> goodsList = orderInfo.getGoods();
                if (goodsList != null && goodsList.size() > 0) {
                    for (OrderGoodsDto goodsDto : goodsList) {
                        MtGoods mtGoods = goodsService.queryGoodsById(goodsDto.getGoodsId());
                        if (mtGoods != null) {
                            // 购买虚拟卡券商品发放处理
                            if (mtGoods.getType().equals(GoodsTypeEnum.COUPON.getKey()) && mtGoods.getCouponIds() != null && StringUtil.isNotEmpty(mtGoods.getCouponIds())) {
                                String couponIds[] = mtGoods.getCouponIds().split(",");
                                if (couponIds.length > 0) {
                                    for (int i = 0; i < couponIds.length; i++) {
                                         userCouponService.buyCouponItem(orderInfo.getId(), Integer.parseInt(couponIds[i]), orderInfo.getUserId(), orderInfo.getUserInfo().getMobile(), goodsDto.getNum());
                                    }
                                }
                            }
                            // 将已销售数量+1
                            goodsService.updateInitSale(mtGoods.getId(), goodsDto.getNum());
                        }
                    }
                }
            } catch (BusinessCheckException e) {
                logger.error("会员购买的卡券发送给会员失败......" + e.getMessage());
            }
        }

        // 处理消费返积分，查询返1积分所需消费金额
        MtSetting setting = settingService.querySettingByName(mtOrder.getMerchantId(), SettingTypeEnum.POINT.getKey(), PointSettingEnum.POINT_NEED_CONSUME.getKey());
        if (setting != null && !orderInfo.getPayType().equals(PayTypeEnum.BALANCE.getKey()) && orderInfo.getIsVisitor().equals(YesOrNoEnum.NO.getKey())) {
            String needPayAmount = setting.getValue();
            Integer needPayAmountInt = Math.round(Integer.parseInt(needPayAmount));
            Double pointNum = 0d;
            if (needPayAmountInt > 0 && orderInfo.getPayAmount().compareTo(new BigDecimal(needPayAmountInt)) >= 0) {
                BigDecimal point = orderInfo.getPayAmount().divide(new BigDecimal(needPayAmountInt), BigDecimal.ROUND_CEILING, 4);
                pointNum = Math.ceil(point.doubleValue());
            }
            logger.info("PaymentService paymentCallback Point orderSn = {} , pointNum ={}", orderInfo.getOrderSn(), pointNum);
            if (pointNum > 0) {
                // 充值是否加倍返积分
                if (orderInfo.getType().equals(OrderTypeEnum.RECHARGE.getKey())) {
                    MtSetting pointSpeedSetting = settingService.querySettingByName(mtOrder.getMerchantId(), SettingTypeEnum.POINT.getKey(), PointSettingEnum.RECHARGE_POINT_SPEED.getKey());
                    if (pointSpeedSetting != null && StringUtil.isNotEmpty(pointSpeedSetting.getValue())) {
                        BigDecimal pointSpeed = new BigDecimal(pointSpeedSetting.getValue());
                        if (pointSpeed.compareTo(new BigDecimal("0")) > 0) {
                            pointNum = pointNum * new Double(pointSpeedSetting.getValue());
                        }
                    }
                }
                MtUser userInfo = memberService.queryMemberById(orderInfo.getUserId());
                MtUserGrade userGrade = userGradeService.queryUserGradeById(orderInfo.getMerchantId(), Integer.parseInt(userInfo.getGradeId()), orderInfo.getUserId());
                // 是否会员积分加倍
                if (userGrade != null && userGrade.getSpeedPoint() > 1) {
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

        // 计算是否要升级（购物订单、付款订单、充值订单）
        if (orderInfo.getIsVisitor().equals(YesOrNoEnum.NO.getKey()) && orderInfo.getType().equals(OrderTypeEnum.GOODS.getKey()) || orderInfo.getType().equals(OrderTypeEnum.PAYMENT.getKey()) || orderInfo.getType().equals(OrderTypeEnum.RECHARGE.getKey())) {
            try {
                if (orderInfo.getIsVisitor().equals(YesOrNoEnum.NO.getKey())) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("STATUS", StatusEnum.ENABLED.getKey());
                    param.put("MERCHANT_ID", mtOrder.getMerchantId());
                    MtUser mtUser = memberService.queryMemberById(orderInfo.getUserId());
                    MtUserGrade mtUserGrade = mtUserGradeMapper.selectById(mtUser.getGradeId());
                    if (mtUserGrade == null) {
                        mtUserGrade = userGradeService.getInitUserGrade(orderInfo.getMerchantId());
                    }
                    List<MtUserGrade> userGradeList = mtUserGradeMapper.selectByMap(param);
                    if (mtUserGrade != null && userGradeList != null && userGradeList.size() > 0) {
                        // 会员已支付金额
                        BigDecimal payMoney = getUserPayMoney(orderInfo.getUserId());
                        // 会员支付订单笔数
                        Integer payOrderCount = getUserPayOrderCount(orderInfo.getUserId());
                        BigDecimal payOrderCountValue = new BigDecimal(payOrderCount);
                        for (MtUserGrade grade : userGradeList) {
                            if (grade.getCatchValue() != null && grade.getCatchType() != null) {
                                // 累计消费金额已达到
                                if (grade.getCatchType().equals(UserGradeCatchTypeEnum.AMOUNT.getKey())) {
                                    if (grade.getGrade().compareTo(mtUserGrade.getGrade()) > 0 && payMoney.compareTo(grade.getCatchValue()) >= 0) {
                                        openGiftService.openGift(mtOrder.getUserId(), grade.getId(), false);
                                    }
                                }
                                // 累计消费次数已达到
                                if (grade.getCatchType().equals(UserGradeCatchTypeEnum.FREQUENCY.getKey()) && payOrderCountValue.compareTo(grade.getCatchValue()) >= 0) {
                                    if (grade.getGrade().compareTo(mtUserGrade.getGrade()) > 0) {
                                        openGiftService.openGift(mtOrder.getUserId(), grade.getId(), false);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error("会员升级出错啦，userId = {}，message = {}", orderInfo.getUserId(), ex.getMessage());
            }
        }

        try {
            // 打印订单
            printerService.printOrder(orderInfo, true);

            // 给商家发送通知短信
            MtStore mtStore = storeService.queryStoreById(mtOrder.getStoreId());
            if (mtStore != null && orderInfo.getIsVisitor().equals(YesOrNoEnum.NO.getKey())) {
                Map<String, String> params = new HashMap<>();
                params.put("orderSn", mtOrder.getOrderSn());
                List<String> mobileList = new ArrayList<>();
                mobileList.add(mtStore.getPhone());
                sendSmsService.sendSms(mtOrder.getMerchantId(), "new-order", mobileList, params);
            }
        } catch (Exception e) {
            logger.info("打印订单或给商家发送短信出错啦，message = {}", e.getMessage());
        }

        return true;
    }

    /**
     * 根据条件搜索订单
     *
     * @param params 查询参数
     * @return
     * */
    @Override
    public List<MtOrder> getOrderListByParams(Map<String, Object> params) {
        return mtOrderMapper.selectByMap(params);
    }

    /**
     * 处理订单详情
     *
     * @param  orderInfo 订单信息
     * @param  needAddress 是否获取订单地址
     * @param  getPayStatus 是否获取支付状态
     * @return UserOrderDto
     * */
    private UserOrderDto getOrderDetail(MtOrder orderInfo, boolean needAddress, boolean getPayStatus) throws BusinessCheckException {
        UserOrderDto userOrderDto = new UserOrderDto();

        userOrderDto.setId(orderInfo.getId());
        userOrderDto.setMerchantId(orderInfo.getMerchantId());
        userOrderDto.setStoreId(orderInfo.getStoreId());
        userOrderDto.setUserId(orderInfo.getUserId());
        userOrderDto.setCouponId(orderInfo.getCouponId());
        userOrderDto.setOrderSn(orderInfo.getOrderSn());
        userOrderDto.setRemark(orderInfo.getRemark());
        userOrderDto.setType(orderInfo.getType());
        userOrderDto.setPayType(orderInfo.getPayType());
        userOrderDto.setOrderMode(orderInfo.getOrderMode());
        userOrderDto.setCreateTime(DateUtil.formatDate(orderInfo.getCreateTime(), "yyyy.MM.dd HH:mm"));
        userOrderDto.setUpdateTime(DateUtil.formatDate(orderInfo.getUpdateTime(), "yyyy.MM.dd HH:mm"));
        userOrderDto.setAmount(orderInfo.getAmount());
        userOrderDto.setIsVisitor(orderInfo.getIsVisitor());
        userOrderDto.setStaffId(orderInfo.getStaffId());
        userOrderDto.setVerifyCode("");
        userOrderDto.setDeliveryFee(orderInfo.getDeliveryFee());
        userOrderDto.setPlatform(orderInfo.getPlatform());
        userOrderDto.setConfirmRemark(orderInfo.getConfirmRemark());
        userOrderDto.setConfirmStatus(orderInfo.getConfirmStatus());
        if (orderInfo.getConfirmTime() != null) {
            userOrderDto.setConfirmTime(DateUtil.formatDate(orderInfo.getConfirmTime(), "yyyy.MM.dd HH:mm"));
        }

        if (orderInfo.getType().equals(OrderTypeEnum.GOODS.getKey()) && orderInfo.getPayStatus().equals(PayStatusEnum.SUCCESS.getKey()) && orderInfo.getConfirmStatus().equals(YesOrNoEnum.NO.getKey())) {
            userOrderDto.setIsVerify(false);
        } else {
            userOrderDto.setIsVerify(true);
        }

        if (orderInfo.getPayAmount() != null) {
            userOrderDto.setPayAmount(orderInfo.getPayAmount());
        } else {
            userOrderDto.setPayAmount(new BigDecimal("0"));
        }

        if (orderInfo.getDiscount() != null) {
            userOrderDto.setDiscount(orderInfo.getDiscount());
        } else {
            userOrderDto.setDiscount(new BigDecimal("0"));
        }

        if (orderInfo.getPointAmount() != null) {
            userOrderDto.setPointAmount(orderInfo.getPointAmount());
        } else {
            userOrderDto.setPointAmount(new BigDecimal("0"));
        }

        userOrderDto.setStatus(orderInfo.getStatus());
        userOrderDto.setParam(orderInfo.getParam());
        userOrderDto.setPayStatus(orderInfo.getPayStatus());

        if (orderInfo.getUsePoint() != null) {
            userOrderDto.setUsePoint(orderInfo.getUsePoint());
        } else {
            userOrderDto.setUsePoint(0);
        }
        if (orderInfo.getPayTime() != null) {
            userOrderDto.setPayTime(DateUtil.formatDate(orderInfo.getPayTime(), "yyyy.MM.dd HH:mm"));
        }

        if (userOrderDto.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            userOrderDto.setTypeName(OrderTypeEnum.PRESTORE.getValue());
        } else if(userOrderDto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) {
            userOrderDto.setTypeName(OrderTypeEnum.PAYMENT.getValue());
        } else if(userOrderDto.getType().equals(OrderTypeEnum.GOODS.getKey())) {
            userOrderDto.setTypeName(OrderTypeEnum.GOODS.getValue());
        } else if(userOrderDto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            userOrderDto.setTypeName(OrderTypeEnum.MEMBER.getValue());
        } else if(userOrderDto.getType().equals(OrderTypeEnum.RECHARGE.getKey())) {
            userOrderDto.setTypeName(OrderTypeEnum.RECHARGE.getValue());
        }

        if (userOrderDto.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
            userOrderDto.setStatusText(OrderStatusEnum.CREATED.getValue());
        } else if(userOrderDto.getStatus().equals(OrderStatusEnum.CANCEL.getKey())) {
            userOrderDto.setStatusText(OrderStatusEnum.CANCEL.getValue());
        } else if(userOrderDto.getStatus().equals(OrderStatusEnum.PAID.getKey())) {
            userOrderDto.setStatusText(OrderStatusEnum.PAID.getValue());
        } else if(userOrderDto.getStatus().equals(OrderStatusEnum.DELIVERY.getKey())) {
            userOrderDto.setStatusText(OrderStatusEnum.DELIVERY.getValue());
        } else if(userOrderDto.getStatus().equals(OrderStatusEnum.DELIVERED.getKey())) {
            userOrderDto.setStatusText(OrderStatusEnum.DELIVERED.getValue());
        } else if(userOrderDto.getStatus().equals(OrderStatusEnum.RECEIVED.getKey())) {
            userOrderDto.setStatusText(OrderStatusEnum.RECEIVED.getValue());
        } else if(userOrderDto.getStatus().equals(OrderStatusEnum.DELETED.getKey())) {
            userOrderDto.setStatusText(OrderStatusEnum.DELETED.getValue());
        } else if(userOrderDto.getStatus().equals(OrderStatusEnum.REFUND.getKey())) {
            userOrderDto.setStatusText(OrderStatusEnum.REFUND.getValue());
        }

        // 订单所属店铺
        MtStore storeInfo = storeService.queryStoreById(orderInfo.getStoreId());
        userOrderDto.setStoreInfo(storeInfo);

        // 所属员工
        if (orderInfo.getStaffId() != null && orderInfo.getStaffId() > 0) {
            MtStaff staffInfo = staffService.queryStaffById(orderInfo.getStaffId());
            userOrderDto.setStaffInfo(staffInfo);
        }

        // 下单用户信息直接取会员个人信息
        OrderUserDto userInfo = new OrderUserDto();
        MtUser user = memberService.queryMemberById(orderInfo.getUserId());
        if (user != null) {
            userInfo.setId(user.getId());
            userInfo.setNo(user.getUserNo());
            userInfo.setName(user.getName());
            if (StringUtil.isNotEmpty(user.getMobile())) {
                userInfo.setMobile(user.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            }
            userInfo.setCardNo(user.getIdcard());
            userInfo.setAddress(user.getAddress());
            userInfo.setOpenId(user.getOpenId());
            userOrderDto.setUserInfo(userInfo);
        }

        List<OrderGoodsDto> goodsList = new ArrayList<>();
        String baseImage = settingService.getUploadBasePath();

        // 储值卡的订单
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            MtCoupon coupon = couponService.queryCouponById(orderInfo.getCouponId());
            if (coupon != null) {
                String[] paramArr = orderInfo.getParam().split(",");
                for (int i = 0; i < paramArr.length; i++) {
                    String[] item = paramArr[i].split("_");
                    if (Integer.parseInt(item[2]) > 0) {
                        OrderGoodsDto goodsDto = new OrderGoodsDto();
                        goodsDto.setId(coupon.getId());
                        goodsDto.setType(OrderTypeEnum.PRESTORE.getKey());
                        goodsDto.setName("预存￥" + item[0] + "到账￥" + item[1]);
                        goodsDto.setNum(Integer.parseInt(item[2]));
                        goodsDto.setPrice(item[0]);
                        goodsDto.setDiscount("0");
                        if (coupon.getImage().indexOf(baseImage) == -1) {
                            goodsDto.setImage(baseImage + coupon.getImage());
                        }
                        goodsList.add(goodsDto);
                    }
                }
            }
        }

        // 商品订单
        if (orderInfo.getType().equals(OrderTypeEnum.GOODS.getKey())) {
            Map<String, Object> params = new HashMap<>();
            params.put("ORDER_ID", orderInfo.getId());
            List<MtOrderGoods> orderGoodsList = mtOrderGoodsMapper.selectByMap(params);
            for (MtOrderGoods orderGoods : orderGoodsList) {
                 MtGoods goodsInfo = mtGoodsMapper.selectById(orderGoods.getGoodsId());
                 if (goodsInfo != null) {
                    OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
                    orderGoodsDto.setId(orderGoods.getId());
                    orderGoodsDto.setName(goodsInfo.getName());
                    if (goodsInfo.getLogo().indexOf(baseImage) == -1) {
                        orderGoodsDto.setImage(baseImage + goodsInfo.getLogo());
                    }
                    orderGoodsDto.setType(OrderTypeEnum.GOODS.getKey());
                    orderGoodsDto.setNum(orderGoods.getNum());
                    orderGoodsDto.setSkuId(orderGoods.getSkuId());
                    orderGoodsDto.setPrice(orderGoods.getPrice().toString());
                    orderGoodsDto.setDiscount(orderGoods.getDiscount().toString());
                    orderGoodsDto.setGoodsId(orderGoods.getGoodsId());
                    if (orderGoods.getSkuId() > 0) {
                        List<GoodsSpecValueDto> specList = goodsService.getSpecListBySkuId(orderGoods.getSkuId());
                        orderGoodsDto.setSpecList(specList);
                    }
                    goodsList.add(orderGoodsDto);
                 }
            }
        }

        // 配送地址
        if (orderInfo.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey()) && needAddress) {
            List<MtOrderAddress> orderAddressList = mtOrderAddressMapper.getOrderAddress(orderInfo.getId());
            MtOrderAddress orderAddress = null;
            if (orderAddressList.size() > 0) {
                orderAddress = orderAddressList.get(0);
            }
            if (orderAddress != null) {
                AddressDto address = new AddressDto();
                address.setId(orderAddress.getId());
                address.setName(orderAddress.getName());
                address.setMobile(orderAddress.getMobile());
                address.setDetail(orderAddress.getDetail());
                address.setProvinceId(orderAddress.getProvinceId());
                address.setCityId(orderAddress.getCityId());
                address.setRegionId(orderAddress.getRegionId());

                if (orderAddress.getProvinceId() > 0) {
                    MtRegion mtProvince = mtRegionMapper.selectById(orderAddress.getProvinceId());
                    if (mtProvince != null) {
                        address.setProvinceName(mtProvince.getName());
                    }
                }
                if (orderAddress.getCityId() > 0) {
                    MtRegion mtCity = mtRegionMapper.selectById(orderAddress.getCityId());
                    if (mtCity != null) {
                        address.setCityName(mtCity.getName());
                    }
                }
                if (orderAddress.getRegionId() > 0) {
                    MtRegion mtRegion = mtRegionMapper.selectById(orderAddress.getRegionId());
                    if (mtRegion != null) {
                        address.setRegionName(mtRegion.getName());
                    }
                }

                userOrderDto.setAddress(address);
            }
        }

        // 物流信息
        if (StringUtil.isNotEmpty(orderInfo.getExpressInfo())) {
            JSONObject express = JSONObject.parseObject(orderInfo.getExpressInfo());
            ExpressDto expressInfo = new ExpressDto();
            expressInfo.setExpressNo(express.get("expressNo").toString());
            expressInfo.setExpressCompany(express.get("expressCompany").toString());
            expressInfo.setExpressTime(express.get("expressTime").toString());
            expressInfo.setExpressCode(express.get("expressCode") == null ? "" : express.get("expressCode").toString());
            userOrderDto.setExpressInfo(expressInfo);
        }

        // 使用的卡券
        if (userOrderDto.getCouponId() != null && userOrderDto.getCouponId() > 0) {
            MtUserCoupon mtUserCoupon = userCouponService.getUserCouponDetail(userOrderDto.getCouponId());
            if (mtUserCoupon != null) {
                MtCoupon mtCoupon = couponService.queryCouponById(mtUserCoupon.getCouponId());
                if (mtCoupon != null) {
                    UserCouponDto couponInfo = new UserCouponDto();
                    couponInfo.setId(mtUserCoupon.getId());
                    couponInfo.setCouponId(mtCoupon.getId());
                    couponInfo.setName(mtCoupon.getName());
                    couponInfo.setAmount(mtUserCoupon.getAmount());
                    couponInfo.setBalance(mtUserCoupon.getBalance());
                    couponInfo.setStatus(mtUserCoupon.getStatus());
                    couponInfo.setType(mtCoupon.getType());
                    userOrderDto.setCouponInfo(couponInfo);
                }
            }
        }

        // 查询支付状态
        if (getPayStatus && !orderInfo.getPayStatus().equals(PayStatusEnum.SUCCESS.getKey())) {
            // 微信支付
            if (orderInfo.getPayType().equals(PayTypeEnum.MICROPAY.getKey()) || orderInfo.getPayType().equals(PayTypeEnum.JSAPI.getKey())) {
                try {
                    Map<String, String> payResult = weixinService.queryPaidOrder(orderInfo.getStoreId(), "", orderInfo.getOrderSn());
                    if (payResult != null && payResult.get("trade_state").equals("SUCCESS")) {
                        BigDecimal payAmount = new BigDecimal(payResult.get("total_fee")).divide(new BigDecimal("100"));
                        setOrderPayed(orderInfo.getId(), payAmount);
                        userOrderDto.setPayStatus(PayStatusEnum.SUCCESS.getKey());
                    }
                } catch (Exception e) {
                    // empty
                }
            }
            // 支付宝支付
            if (orderInfo.getPayType().equals(PayTypeEnum.ALISCAN.getKey())) {
                try {
                    Map<String, String> payResult = alipayService.queryPaidOrder(orderInfo.getStoreId(), "", orderInfo.getOrderSn());
                    if (payResult != null) {
                        BigDecimal payAmount = new BigDecimal(payResult.get("payAmount"));
                        setOrderPayed(orderInfo.getId(), payAmount);
                        userOrderDto.setPayStatus(PayStatusEnum.SUCCESS.getKey());
                    }
                } catch (Exception e) {
                    // empty
                }
            }
        }
        userOrderDto.setGoods(goodsList);
        return userOrderDto;
    }

    /**
     * 获取订单总数
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     * */
    @Override
    public BigDecimal getOrderCount(Integer merchantId, Integer storeId) {
        if (storeId != null && storeId > 0) {
            return mtOrderMapper.getStoreOrderCount(storeId);
        } else {
            return mtOrderMapper.getOrderCount(merchantId);
        }
    }

    /**
     * 获取订单数量
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     * */
    @Override
    public BigDecimal getOrderCount(Integer merchantId, Integer storeId, Date beginTime, Date endTime) {
        if (storeId != null && storeId > 0) {
            return mtOrderMapper.getStoreOrderCountByTime(storeId, beginTime, endTime);
        } else {
            return mtOrderMapper.getOrderCountByTime(merchantId, beginTime, endTime);
        }
    }

    /**
     * 获取支付金额
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     * */
    @Override
    public BigDecimal getPayMoney(Integer merchantId, Integer storeId, Date beginTime, Date endTime) {
        BigDecimal payMoney;
        if (storeId != null && storeId > 0) {
            payMoney = mtOrderMapper.getStorePayMoneyByTime(storeId, beginTime, endTime);
        } else {
            payMoney = mtOrderMapper.getPayMoneyByTime(merchantId, beginTime, endTime);
        }
        if (payMoney == null) {
            return new BigDecimal("0");
        }
        return payMoney;
    }

    /**
     * 获取支付人数
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     * */
    @Override
    public Integer getPayUserCount(Integer merchantId, Integer storeId) {
        if (storeId != null && storeId > 0) {
            return mtOrderMapper.getStorePayUserCount(storeId);
        } else {
            return mtOrderMapper.getPayUserCount(merchantId);
        }
    }

    /**
     * 获取支付总金额
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     * */
    @Override
    public BigDecimal getPayMoney(Integer merchantId, Integer storeId) {
        if (storeId != null && storeId > 0) {
            return mtOrderMapper.getStorePayMoney(storeId);
        } else {
            return mtOrderMapper.getPayMoney(merchantId);
        }
    }

    /**
     * 计算商品总价
     *
     * @param merchantId 商户ID
     * @param userId 会员ID
     * @param cartList 购物车列表
     * @param couponId 卡券ID
     * @param isUsePoint 使用积分数量
     * @param orderMode 订单模式
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public Map<String, Object> calculateCartGoods(Integer merchantId, Integer userId, List<MtCart> cartList, Integer couponId, boolean isUsePoint, String platform, String orderMode) throws BusinessCheckException {
        MtUser userInfo = memberService.queryMemberById(userId);

        // 设置是否不能用积分抵扣
        MtSetting pointSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.POINT.getKey(), PointSettingEnum.CAN_USE_AS_MONEY.getKey());
        if (pointSetting != null && !pointSetting.getValue().equals(YesOrNoEnum.TRUE.getKey())) {
            isUsePoint = false;
        }

        List<ResCartDto> cartDtoList = new ArrayList<>();
        String basePath = settingService.getUploadBasePath();
        Integer totalNum = 0;
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalCanUsePointAmount = new BigDecimal("0");
        BigDecimal memberDiscount = new BigDecimal("0");
        BigDecimal percent = new BigDecimal("0");
        Integer storeId = 0;
        if (cartList.size() > 0) {
            // 会员折扣
            MtUserGrade userGrade = userGradeService.queryUserGradeById(userInfo.getMerchantId(), userInfo.getGradeId() != null ? Integer.parseInt(userInfo.getGradeId()) : 1, userId);
            if (userGrade != null && userGrade.getDiscount() != null && userGrade.getDiscount() > 0 && !userInfo.getIsStaff().equals(YesOrNoEnum.YES.getKey())) {
                percent = new BigDecimal(userGrade.getDiscount()).divide(new BigDecimal("10"), BigDecimal.ROUND_CEILING, 4);
                if (percent.compareTo(new BigDecimal("0")) <= 0) {
                    percent = new BigDecimal("1");
                }
            }
            for (MtCart cart : cartList) {
                if (storeId <= 0 && cart.getStoreId() != null) {
                    storeId = cart.getStoreId();
                }
                // 购物车商品信息
                MtGoods mtGoodsInfo = goodsService.queryGoodsById(cart.getGoodsId());
                if (mtGoodsInfo == null || !mtGoodsInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    continue;
                }
                // 取对应sku的价格
                if (cart.getSkuId() != null && cart.getSkuId() > 0) {
                    MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(cart.getSkuId());
                    if (mtGoodsSku != null && mtGoodsSku.getPrice().compareTo(new BigDecimal("0")) > 0) {
                        mtGoodsInfo.setPrice(mtGoodsSku.getPrice());
                    }
                }
                // 会员支付折扣
                boolean isDiscount = mtGoodsInfo.getIsMemberDiscount().equals(YesOrNoEnum.YES.getKey()) ? true : false;
                if (percent.compareTo(new BigDecimal("0")) > 0 && isDiscount) {
                    BigDecimal discount = mtGoodsInfo.getPrice().subtract(mtGoodsInfo.getPrice().multiply(percent)).multiply(new BigDecimal(cart.getNum()));
                    memberDiscount = memberDiscount.add(discount);
                }
                totalNum = totalNum + cart.getNum();
                ResCartDto cartDto = new ResCartDto();
                cartDto.setId(cart.getId());
                cartDto.setGoodsId(cart.getGoodsId());
                cartDto.setNum(cart.getNum());
                cartDto.setSkuId(cart.getSkuId());
                cartDto.setUserId(cart.getUserId());
                // 购物车是否有效
                Boolean isEffect = true;
                if (cart.getSkuId() > 0) {
                    List<GoodsSpecValueDto> specList = goodsService.getSpecListBySkuId(cart.getSkuId());
                    cartDto.setSpecList(specList);
                }
                if (StringUtil.isNotEmpty(mtGoodsInfo.getLogo()) && (mtGoodsInfo.getLogo().indexOf(basePath) == -1)) {
                    mtGoodsInfo.setLogo(basePath + mtGoodsInfo.getLogo());
                }
                // 读取sku的数据
                if (cart.getSkuId() > 0) {
                    MtGoods mtGoods = new MtGoods();
                    BeanUtils.copyProperties(mtGoodsInfo, mtGoods);
                    MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(cart.getSkuId());
                    if (mtGoodsSku != null) {
                        if (StringUtil.isNotEmpty(mtGoodsSku.getLogo()) && (mtGoodsSku.getLogo().indexOf(basePath) == -1)) {
                            mtGoods.setLogo(basePath + mtGoodsSku.getLogo());
                        }
                        if (mtGoodsSku.getWeight().compareTo(new BigDecimal("0")) > 0) {
                            mtGoods.setWeight(mtGoodsSku.getWeight());
                        }
                        mtGoods.setPrice(mtGoodsSku.getPrice());
                        mtGoods.setLinePrice(mtGoodsSku.getLinePrice());
                        mtGoods.setStock(mtGoodsSku.getStock());
                    }
                    cartDto.setGoodsInfo(mtGoods);
                } else {
                    cartDto.setGoodsInfo(mtGoodsInfo);
                }
                if (mtGoodsInfo.getStock() != null && mtGoodsInfo.getStock() < cartDto.getNum()) {
                    isEffect = false;
                }
                cartDto.setIsEffect(isEffect);
                // 计算总价
                totalPrice = totalPrice.add(cartDto.getGoodsInfo().getPrice().multiply(new BigDecimal(cart.getNum())));
                // 累加可用积分去抵扣的金额
                if (mtGoodsInfo.getCanUsePoint() != null && mtGoodsInfo.getCanUsePoint().equals(YesOrNoEnum.YES.getKey())) {
                    totalCanUsePointAmount = totalCanUsePointAmount.add(cartDto.getGoodsInfo().getPrice().multiply(new BigDecimal(cart.getNum())));
                }
                cartDtoList.add(cartDto);
            }
        }

        Map<String, Object> result = new HashMap<>();

        // 可用卡券列表
        List<CouponDto> couponList = new ArrayList<>();
        List<String> statusList = Arrays.asList(UserCouponStatusEnum.UNUSED.getKey());
        List<MtUserCoupon> userCouponList = userCouponService.getUserCouponList(userId, statusList);
        if (userCouponList.size() > 0) {
            for (MtUserCoupon userCoupon : userCouponList) {
                MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());
                // 优惠券和储值卡才能使用
                if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey()) || couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                    CouponDto couponDto = new CouponDto();
                    couponDto.setId(couponInfo.getId());
                    couponDto.setUserCouponId(userCoupon.getId());
                    couponDto.setName(couponInfo.getName());
                    couponDto.setAmount(userCoupon.getAmount());
                    couponDto.setStatus(UserCouponStatusEnum.DISABLE.getKey());
                    // 购物不能用专用的卡券
                    if (couponInfo.getUseFor() != null && StringUtil.isNotEmpty(couponInfo.getUseFor())) {
                        if (couponInfo.getUseFor().equals(CouponUseForEnum.MEMBER_GRADE.getKey())) {
                            continue;
                        }
                        if (couponInfo.getUseFor().equals(CouponUseForEnum.OFF_LINE_PAYMENT.getKey())) {
                            // 只有PC收银端能用
                            if (!platform.equals(PlatformTypeEnum.PC.getCode())) {
                                continue;
                            }
                        }
                    }
                    // 判断在当前门店是否适用
                    if (StringUtil.isNotEmpty(couponInfo.getStoreIds())) {
                        String[] storeIds = couponInfo.getStoreIds().split(",");
                        if (storeIds.length > 0 && storeId != null && !Arrays.asList(storeIds).contains(storeId.toString())) {
                            continue;
                        }
                    }
                    boolean isEffective = couponService.isCouponEffective(couponInfo, userCoupon);
                    // 优惠券
                    if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                        couponDto.setType(CouponTypeEnum.COUPON.getValue());
                        if (StringUtil.isEmpty(couponInfo.getOutRule()) || couponInfo.getOutRule().equals("0")) {
                            couponDto.setDescription("无使用门槛");
                            if (isEffective) {
                                couponDto.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                            }
                        } else {
                            couponDto.setDescription("满" + couponInfo.getOutRule() + "元可用");
                            BigDecimal conditionAmount = new BigDecimal(couponInfo.getOutRule());
                            if (totalPrice.compareTo(conditionAmount) > 0 && isEffective) {
                                couponDto.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                            }
                        }
                    } else if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                        // 储值卡
                        couponDto.setType(CouponTypeEnum.PRESTORE.getValue());
                        couponDto.setDescription("无使用门槛");
                        couponDto.setAmount(userCoupon.getBalance());
                        // 余额须大于0
                        if (isEffective && (userCoupon.getBalance().compareTo(new BigDecimal("0")) > 0)) {
                            couponDto.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                        }
                    }
                    // 适用商品
                    if (userCoupon != null) {
                        if (couponInfo.getApplyGoods() != null && couponInfo.getApplyGoods().equals(ApplyGoodsEnum.PARK_GOODS.getKey())) {
                            List<MtCouponGoods> couponGoodsList = mtCouponGoodsMapper.getCouponGoods(couponInfo.getId());
                            if (couponGoodsList != null && couponGoodsList.size() > 0 && cartList.size() > 0) {
                                List<Integer> applyGoodsIds = new ArrayList<>();
                                List<Integer> goodsIds = new ArrayList<>();
                                for (MtCouponGoods mtCouponGoods : couponGoodsList) {
                                    applyGoodsIds.add(mtCouponGoods.getGoodsId());
                                }
                                for (MtCart mtCart : cartList) {
                                    goodsIds.add(mtCart.getGoodsId());
                                }
                                List<Integer> intersection = applyGoodsIds.stream()
                                        .filter(goodsIds::contains)
                                        .collect(Collectors.toList());
                                if (intersection.size() == 0) {
                                    couponDto.setStatus(UserCouponStatusEnum.DISABLE.getKey());
                                }
                            }
                        }
                    }
                    if (couponInfo.getExpireType().equals(CouponExpireTypeEnum.FIX.getKey())) {
                        couponDto.setEffectiveDate(DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd HH:mm") + "~" + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd HH:mm"));
                    }
                    if (couponInfo.getExpireType().equals(CouponExpireTypeEnum.FLEX.getKey())) {
                        couponDto.setEffectiveDate(DateUtil.formatDate(userCoupon.getCreateTime(), "yyyy.MM.dd HH:mm") + "~" + DateUtil.formatDate(userCoupon.getExpireTime(), "yyyy.MM.dd HH:mm"));
                    }
                    couponList.add(couponDto);
                }
            }
        }

        // 使用的卡券
        MtCoupon useCouponInfo = null;
        BigDecimal couponAmount = new BigDecimal("0");
        if (couponId > 0) {
            MtUserCoupon userCouponInfo = userCouponService.getUserCouponDetail(couponId);
            if (userCouponInfo != null) {
                useCouponInfo = couponService.queryCouponById(userCouponInfo.getCouponId());
                boolean isEffective = couponService.isCouponEffective(useCouponInfo, userCouponInfo);
                if (isEffective) {
                   if (useCouponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                       couponAmount = useCouponInfo.getAmount();
                   } else if(useCouponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                       BigDecimal couponTotalAmount = userCouponInfo.getBalance();
                       if (couponTotalAmount.compareTo(totalPrice) > 0) {
                           couponAmount = totalPrice;
                           useCouponInfo.setAmount(totalPrice);
                       } else {
                           couponAmount = couponTotalAmount;
                           useCouponInfo.setAmount(couponTotalAmount);
                       }
                   }
                }
            }
        }

        // 支付金额 = 商品总额 - 卡券抵扣金额
        BigDecimal payPrice = totalPrice.subtract(couponAmount);

        // 可用积分、可用积分金额
        Integer myPoint = userInfo.getPoint() == null ? 0 : userInfo.getPoint();
        Integer usePoint = 0;
        BigDecimal usePointAmount = new BigDecimal("0");
        MtSetting setting = settingService.querySettingByName(merchantId, SettingTypeEnum.POINT.getKey(), PointSettingEnum.EXCHANGE_NEED_POINT.getKey());
        if (myPoint > 0 && setting != null && isUsePoint) {
            if (StringUtil.isNotEmpty(setting.getValue()) && !setting.getValue().equals("0")) {
                BigDecimal usePoints = new BigDecimal(myPoint);
                usePointAmount = usePoints.divide(new BigDecimal(setting.getValue()), BigDecimal.ROUND_CEILING, 4);
                usePoint = myPoint;
                if (usePointAmount.compareTo(totalCanUsePointAmount) >= 0) {
                    usePointAmount = totalCanUsePointAmount;
                    usePoint = totalCanUsePointAmount.multiply(new BigDecimal(setting.getValue())).intValue();
                }
            }
        }

        // 积分金额不能大于支付金额
        if (usePointAmount.compareTo(payPrice) > 0 && isUsePoint) {
            usePointAmount = payPrice;
            BigDecimal usePoints = payPrice.multiply(new BigDecimal(setting.getValue()));
            usePoint = usePoints.intValue();
        }

        // 支付金额 = 商品总额 - 积分抵扣金额
        payPrice = payPrice.subtract(usePointAmount);

        // 配送费用
        BigDecimal deliveryFee = new BigDecimal("0");
        MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.ORDER.getKey(), OrderSettingEnum.DELIVERY_FEE.getKey());
        if (mtSetting != null && StringUtil.isNotEmpty(mtSetting.getValue()) && orderMode.equals(OrderModeEnum.EXPRESS.getKey())) {
            deliveryFee = new BigDecimal(mtSetting.getValue());
        }

        payPrice = payPrice.add(deliveryFee).subtract(memberDiscount);
        BigDecimal discount = totalPrice.subtract(payPrice).divide(new BigDecimal("10"), BigDecimal.ROUND_CEILING, 2);

        if (payPrice.compareTo(new BigDecimal("0")) < 0) {
            payPrice = new BigDecimal("0");
        }

        result.put("list", cartDtoList);
        result.put("totalNum", totalNum);
        result.put("totalPrice", totalPrice);
        result.put("payPrice", payPrice);
        result.put("couponList", couponList);
        result.put("useCouponInfo", useCouponInfo);
        result.put("usePoint", usePoint);
        result.put("myPoint", myPoint);
        result.put("couponAmount", couponAmount);
        result.put("usePointAmount", usePointAmount);
        result.put("deliveryFee", deliveryFee);
        result.put("discount", discount);
        if (memberDiscount.compareTo(new BigDecimal("0")) > 0) {
            result.put("memberDiscount", (new BigDecimal("10").multiply(percent)));
        } else {
            result.put("memberDiscount", 0);
        }

        return result;
    }

    /**
     * 获取会员支付金额
     *
     * @param userId 会员ID
     * @return
     * */
    @Override
    public BigDecimal getUserPayMoney(Integer userId) {
        return mtOrderMapper.getUserPayMoney(userId);
    }

    /**
     * 获取会员订单数
     *
     * @param userId 会员ID
     * @return
     * */
    @Override
    public Integer getUserPayOrderCount(Integer userId) {
        return mtOrderMapper.getUserPayOrderCount(userId);
    }

    /**
     * 获取等待分佣的订单列表
     *
     * @param dateTime 时间
     * @return
     * */
    @Override
    public List<MtOrder> getTobeCommissionOrderList(String dateTime) {
        return mtOrderMapper.getTobeCommissionOrderList(dateTime);
    }

    /**
     * 提交充值订单
     *
     * @param request HttpServletRequest对象
     * @param rechargeParam 充值参数
     * @return
     * */
    @Override
    public MtOrder doRecharge(HttpServletRequest request, RechargeParam rechargeParam) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        String platform = request.getHeader("platform") == null ? "" : request.getHeader("platform");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        String rechargeAmount = rechargeParam.getRechargeAmount() == null ? "" : rechargeParam.getRechargeAmount();
        String customAmount = rechargeParam.getCustomAmount() == null ? "" : rechargeParam.getCustomAmount();
        if (StringUtil.isEmpty(rechargeAmount) && StringUtil.isEmpty(customAmount)) {
            throw new BusinessCheckException("请确认充值金额");
        }
        if (rechargeParam.getMemberId() == null || rechargeParam.getMemberId() < 1) {
            throw new BusinessCheckException("请确认充值会员信息");
        }

        Integer merchantId = merchantService.getMerchantId(merchantNo);

        // 充值赠送金额
        String ruleParam = "";
        MtSetting mtSetting = settingService.querySettingByName(merchantId, SettingTypeEnum.BALANCE.getKey(), BalanceSettingEnum.RECHARGE_RULE.getKey());
        if (StringUtil.isNotEmpty(rechargeAmount) && mtSetting != null) {
            if (mtSetting.getValue() != null && StringUtil.isNotEmpty(mtSetting.getValue())) {
                String rules[] = mtSetting.getValue().split(",");
                for (String rule : rules) {
                    String amountArr[] = rule.split("_");
                    if (amountArr.length == 2) {
                        if (amountArr[0].equals(rechargeAmount)) {
                            ruleParam = rule;
                            break;
                        }
                    }
                }
            }
        }

        // 自定义充值没有赠送金额
        if (StringUtil.isNotEmpty(customAmount) && Integer.parseInt(customAmount) > 0 && (StringUtil.isEmpty(rechargeAmount) || Integer.parseInt(rechargeAmount) <= 0)) {
            rechargeAmount = customAmount;
            ruleParam = customAmount + "_0";
        }

        if (StringUtil.isEmpty(ruleParam)) {
            ruleParam = rechargeAmount + "_0";
        }

        BigDecimal amount = new BigDecimal(rechargeAmount);
        if (amount.compareTo(new BigDecimal("0")) <= 0) {
            throw new BusinessCheckException("请确认充值金额");
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setType(OrderTypeEnum.RECHARGE.getKey());
        orderDto.setUserId(rechargeParam.getMemberId());
        orderDto.setStoreId(storeId);
        orderDto.setAmount(amount);
        orderDto.setUsePoint(0);
        orderDto.setRemark("会员充值");
        orderDto.setParam(ruleParam);
        orderDto.setStatus(OrderStatusEnum.CREATED.getKey());
        orderDto.setPayStatus(PayStatusEnum.WAIT.getKey());
        orderDto.setPointAmount(new BigDecimal("0"));
        orderDto.setOrderMode("");
        orderDto.setCouponId(0);
        orderDto.setPlatform(platform);
        orderDto.setMerchantId(merchantId);

        return saveOrder(orderDto);
    }
}
