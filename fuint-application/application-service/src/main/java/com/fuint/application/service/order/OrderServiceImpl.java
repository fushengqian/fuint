package com.fuint.application.service.order;

import com.alibaba.fastjson.JSONObject;
import com.fuint.application.BaseService;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.*;
import com.fuint.application.dto.*;
import com.fuint.application.enums.*;
import com.fuint.application.service.address.AddressService;
import com.fuint.application.service.cart.CartService;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.goods.GoodsService;
import com.fuint.application.service.member.MemberService;
import com.fuint.application.service.point.PointService;
import com.fuint.application.service.setting.SettingService;
import com.fuint.application.service.store.StoreService;
import com.fuint.application.service.usercoupon.UserCouponService;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import com.fuint.util.StringUtil;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class OrderServiceImpl extends BaseService implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private MtOrderRepository orderRepository;

    @Autowired
    private MtGoodsRepository goodsRepository;

    @Autowired
    private MtOrderGoodsRepository orderGoodsRepository;

    @Autowired
    private MtCartRepository cartRepository;

    @Autowired
    private MtOrderAddressRepository orderAddressRepository;

    @Autowired
    private MtConfirmLogRepository confirmLogRepository;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

    @Autowired
    private MtGoodsSkuRepository goodsSkuRepository;

    @Autowired
    private MtRegionRepository regionRepository;

    @Autowired
    private SettingService settingService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PointService pointService;

    @Autowired
    private CartService cartService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StoreService storeService;

    /**
     * 获取用户订单列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject getUserOrderList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "" : paramMap.get("userId").toString();
        String storeId = paramMap.get("storeId") == null ? "" : paramMap.get("storeId").toString();
        String status =  paramMap.get("status") == null ? "": paramMap.get("status").toString();
        String payStatus =  paramMap.get("payStatus") == null ? "": paramMap.get("payStatus").toString();
        String dataType =  paramMap.get("dataType") == null ? "": paramMap.get("dataType").toString();
        String type =  paramMap.get("type") == null ? "": paramMap.get("type").toString();
        String orderSn =  paramMap.get("orderSn") == null ? "": paramMap.get("orderSn").toString();
        String mobile =  paramMap.get("mobile") == null ? "": paramMap.get("mobile").toString();
        String orderMode =  paramMap.get("orderMode") == null ? "": paramMap.get("orderMode").toString();

        if (dataType.equals("pay")) {
            status = OrderStatusEnum.CREATED.getKey();// 待支付
        } else if(dataType.equals("paid")) {
            status = OrderStatusEnum.PAID.getKey(); // 已支付
        } else if(dataType.equals("cancel")) {
            status = OrderStatusEnum.CANCEL.getKey(); // 已取消
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        if (StringUtil.isNotEmpty(orderSn)) {
            searchParams.put("EQ_orderSn", orderSn);
        }
        if (StringUtil.isNotEmpty(status)) {
            searchParams.put("EQ_status", status);
        }
        if (StringUtil.isNotEmpty(payStatus)) {
            searchParams.put("EQ_payStatus", payStatus);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(mobile);
            userId = userInfo.getId()+"";
        }
        if (StringUtil.isNotEmpty(userId)) {
            searchParams.put("EQ_userId", userId+"");
        }
        if (StringUtil.isNotEmpty(storeId)) {
            searchParams.put("EQ_storeId", storeId+"");
        }
        if (StringUtil.isNotEmpty(type)) {
            searchParams.put("EQ_type", type);
        }
        if (StringUtil.isNotEmpty(orderMode)) {
            searchParams.put("EQ_orderMode", orderMode);
        }

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"createTime desc", "status asc"});
        paginationRequest.getSearchParams().put("NQ_status", OrderStatusEnum.DELETED.getKey());
        PaginationResponse<MtOrder> paginationResponse = orderRepository.findResultsByPagination(paginationRequest);

        List<UserOrderDto> dataList = new ArrayList<>();
        if (paginationResponse.getContent().size() > 0) {
            for (MtOrder order : paginationResponse.getContent()) {
                UserOrderDto dto = this.getOrderDetail(order, false);
                dataList.add(dto);
            }
        }

        Long total = paginationResponse.getTotalElements();
        PageRequest pageRequest = new PageRequest(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        Page page = new PageImpl(dataList, pageRequest, total.longValue());
        PaginationResponse<UserOrderDto> pageResponse = new PaginationResponse(page, UserOrderDto.class);
        pageResponse.setContent(page.getContent());
        pageResponse.setCurrentPage(pageResponse.getCurrentPage() + 1);
        pageResponse.setTotalPages(paginationResponse.getTotalPages());

        return getSuccessResult(pageResponse);
    }

    /**
     * 保存订单信息
     *
     * @param orderDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "保存订单信息")
    public MtOrder saveOrder(OrderDto orderDto) throws BusinessCheckException {
        MtOrder MtOrder;
        if (null != orderDto.getId() && orderDto.getId() > 0) {
            MtOrder = orderRepository.findOne(orderDto.getId());
        } else {
            MtOrder = new MtOrder();
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
            MtOrder.setOrderSn(orderSn);
        } else {
            orderSn = MtOrder.getOrderSn();
        }

        MtOrder.setUserId(orderDto.getUserId());
        MtOrder.setStoreId(orderDto.getStoreId());
        MtOrder.setCouponId(orderDto.getCouponId());
        MtOrder.setParam(orderDto.getParam());
        MtOrder.setRemark(orderDto.getRemark());
        MtOrder.setStatus(OrderStatusEnum.CREATED.getKey());
        MtOrder.setType(orderDto.getType());
        MtOrder.setAmount(orderDto.getAmount());
        MtOrder.setPayAmount(orderDto.getPayAmount());
        MtOrder.setDiscount(orderDto.getDiscount());
        MtOrder.setPayStatus(PayStatusEnum.WAIT.getKey());
        MtOrder.setPointAmount(orderDto.getPointAmount());
        MtOrder.setUsePoint(orderDto.getUsePoint());
        MtOrder.setOrderMode(orderDto.getOrderMode());
        MtOrder.setPayType(orderDto.getPayType());
        MtOrder.setOperator(orderDto.getOperator());

        // 首先生成订单
        MtOrder orderInfo = orderRepository.save(MtOrder);
        MtOrder.setId(orderInfo.getId());

        // 如果没有指定店铺，则读取默认的店铺
        if (orderDto.getStoreId() == null || orderDto.getStoreId() <= 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("EQ_status", StatusEnum.ENABLED.getKey());
            params.put("EQ_isDefault", "Y");
            List<MtStore> storeList = storeService.queryStoresByParams(params);
            if (storeList.size() > 0) {
                MtOrder.setStoreId(storeList.get(0).getId());
            } else {
                MtOrder.setStoreId(0);
            }
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
                logger.error("扣减会员积分出错：" + e.getMessage());
            }
        }

        MtOrder.setUpdateTime(new Date());
        MtOrder.setCreateTime(new Date());

        // 计算商品订单总金额
        List<MtCart> cartList = new ArrayList<>();
        Map<String, Object> cartData = new HashMap<>();
        if (orderDto.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
            Map<String, Object> param = new HashMap<>();
            param.put("EQ_status", StatusEnum.ENABLED.getKey());
            if (StringUtil.isNotEmpty(orderDto.getCartIds())) {
                param.put("IN_id", orderDto.getCartIds());
            }
            if (orderDto.getGoodsId() < 1) {
                cartList = cartService.queryCartListByParams(param);
                if (cartList.size() < 1) {
                    throw new BusinessCheckException("生成订单失败，请稍后重试");
                }
            } else {
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
            cartData = this.calculateCartGoods(orderDto.getUserId(), cartList, orderDto.getCouponId(), isUsePoint);

            MtOrder.setAmount(new BigDecimal(cartData.get("totalPrice").toString()));
            MtOrder.setUsePoint(Integer.parseInt(cartData.get("usePoint").toString()));
            MtOrder.setDiscount(new BigDecimal(cartData.get("couponAmount").toString()));

            // 实付金额
            BigDecimal payAmount = MtOrder.getAmount().subtract(MtOrder.getPointAmount()).subtract(MtOrder.getDiscount());
            if (payAmount.compareTo(new BigDecimal("0")) > 0) {
                MtOrder.setPayAmount(payAmount);
            } else {
                MtOrder.setPayAmount(new BigDecimal("0"));
            }

            // 购物使用了卡券
            if (MtOrder.getCouponId() > 0 && (MtOrder.getDiscount().compareTo(new BigDecimal("0")) > 0)) {
                String useCode = couponService.useCoupon(MtOrder.getCouponId(), MtOrder.getUserId(), MtOrder.getStoreId(), MtOrder.getId(), MtOrder.getDiscount(), "购物使用卡券");
                // 卡券使用失败
                if (StringUtil.isEmpty(useCode)) {
                    MtOrder.setDiscount(new BigDecimal("0"));
                    MtOrder.setCouponId(0);
                }
            }
        }

        // 再次更新订单
        orderInfo = orderRepository.save(MtOrder);
        if (orderInfo == null) {
            throw new BusinessCheckException("生成订单失败，请稍后重试");
        }

        // 如果是商品订单，生成订单商品
        if (orderDto.getType().equals(OrderTypeEnum.GOOGS.getKey()) && cartList.size() > 0) {
            Object listObject = cartData.get("list");
            List<ResCartDto> lists =(ArrayList<ResCartDto>)listObject;
            for (ResCartDto cart : lists) {
                 MtOrderGoods orderGoods = new MtOrderGoods();
                 orderGoods.setOrderId(orderInfo.getId());
                 orderGoods.setGoodsId(cart.getGoodsId());
                 orderGoods.setSkuId(cart.getSkuId());
                 orderGoods.setNum(cart.getNum());
                 orderGoods.setPrice(cart.getGoodsInfo().getPrice());
                 orderGoods.setDiscount(new BigDecimal("0"));
                 orderGoods.setStatus(StatusEnum.ENABLED.getKey());
                 orderGoods.setCreateTime(new Date());
                 orderGoods.setUpdateTime(new Date());
                 orderGoodsRepository.save(orderGoods);
                 // 扣减库存
                 MtGoods goodsInfo = goodsRepository.findOne(cart.getGoodsId());
                 if (goodsInfo.getIsSingleSpec().equals("Y")) {
                     // 单规格
                     goodsInfo.setStock(goodsInfo.getStock() - cart.getNum());
                     goodsRepository.save(goodsInfo);
                 } else {
                     // 多规格减去库存
                     MtGoodsSku mtGoodsSku = goodsSkuRepository.findOne(cart.getSkuId());
                     if (mtGoodsSku != null) {
                         mtGoodsSku.setStock(mtGoodsSku.getStock() - cart.getNum());
                         goodsSkuRepository.save(mtGoodsSku);
                     }
                 }
                 if (cart.getId() > 0) {
                    cartRepository.delete(cart.getId());
                 }
            }

            // 需要配送的订单，生成配送地址
            if (orderDto.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey())) {
                Map<String, Object> params = new HashMap<>();
                params.put("userId", orderDto.getUserId().toString());
                params.put("isDefault", "Y");
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
                orderAddressRepository.save(orderAddress);
            }
        }

        return orderInfo;
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @throws BusinessCheckException
     */
    @Override
    public UserOrderDto getOrderById(Integer id) throws BusinessCheckException {
        MtOrder orderInfo = orderRepository.findOne(id);
        return this.getOrderDetail(orderInfo, true);
    }

    /**
     * 取消订单
     * @param id 订单ID
     * @return
     * */
    @Override
    @Transactional
    @OperationServiceLog(description = "取消订单")
    public MtOrder cancelOrder(Integer id, String remark) throws BusinessCheckException {
        MtOrder mtOrder = orderRepository.findOne(id);

        if (mtOrder != null && mtOrder.getStatus().equals(OrderStatusEnum.CREATED.getKey()) && mtOrder.getPayStatus().equals(PayStatusEnum.WAIT.getKey())) {
            if (StringUtil.isNotEmpty(remark)) {
                mtOrder.setRemark(remark);
            }

            mtOrder.setStatus(OrderStatusEnum.CANCEL.getKey());
            mtOrder = orderRepository.save(mtOrder);

            // 返回积分
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
            List<MtConfirmLog> confirmLogList = confirmLogRepository.getOrderConfirmLogList(mtOrder.getId());
            if (confirmLogList.size() > 0) {
                for (MtConfirmLog log : confirmLogList) {
                    MtCoupon couponInfo = couponService.queryCouponById(log.getCouponId());
                    MtUserCoupon userCouponInfo = userCouponRepository.findOne(log.getUserCouponId());

                    if (userCouponInfo != null) {
                        // 优惠券直接置为未使用
                        if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                            userCouponInfo.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                            userCouponRepository.save(userCouponInfo);
                        }

                        // 预存卡把余额加回去
                        if (couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                            BigDecimal balance = userCouponInfo.getBalance();
                            BigDecimal newBalance = balance.add(log.getAmount());
                            if (newBalance.compareTo(userCouponInfo.getAmount()) <= 0) {
                                userCouponInfo.setBalance(newBalance);
                                userCouponInfo.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                            }
                            userCouponRepository.save(userCouponInfo);
                        }

                        // 撤销核销记录
                        log.setStatus(StatusEnum.DISABLE.getKey());
                        confirmLogRepository.save(log);
                    }
                }
            }
        }

        return mtOrder;
    }

    /**
     * 根据订单ID删除
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除订单")
    public void deleteOrder(Integer id, String operator) {
        MtOrder mtOrder = orderRepository.findOne(id);
        if (mtOrder == null) {
            return;
        }

        mtOrder.setStatus(OrderStatusEnum.DELETED.getKey());
        mtOrder.setUpdateTime(new Date());
        mtOrder.setOperator(operator);

        orderRepository.save(mtOrder);
    }

    /**
     * 根据订单号获取订单详情
     *
     * @param  orderSn 订单号
     * @throws BusinessCheckException
     */
    @Override
    public UserOrderDto getOrderByOrderSn(String orderSn) throws BusinessCheckException {
        MtOrder orderInfo = orderRepository.findByOrderSn(orderSn);
        if (orderInfo == null) {
            return null;
        }

        return this.getOrderDetail(orderInfo, true);
    }

    /**
     * 更新订单
     *
     * @param orderDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "更新订单")
    public MtOrder updateOrder(OrderDto orderDto) throws BusinessCheckException {
        MtOrder MtOrder = orderRepository.findOne(orderDto.getId());
        if (null == MtOrder || StatusEnum.DISABLE.getKey().equals(MtOrder.getStatus())) {
            logger.error("修改订单状态异常：" + orderDto.toString());
            throw new BusinessCheckException("该订单状态异常");
        }

        MtOrder.setId(orderDto.getId());
        MtOrder.setUpdateTime(new Date());

        if (null != orderDto.getOperator()) {
            MtOrder.setOperator(orderDto.getOperator());
        }

        if (null != orderDto.getStatus()) {
            MtOrder.setStatus(orderDto.getStatus());
            // 订单状态为已支付，修改支付状态
            if (orderDto.getStatus().equals(OrderStatusEnum.PAID.getKey())) {
                orderDto.setPayStatus(PayStatusEnum.SUCCESS.getKey());
                orderDto.setPayTime(new Date());
            }
            if (orderDto.getStatus().equals(OrderStatusEnum.CANCEL.getKey()) || orderDto.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
                orderDto.setPayStatus(PayStatusEnum.WAIT.getKey());
            }
        }

        if (null != orderDto.getPayAmount()) {
            MtOrder.setPayAmount(orderDto.getPayAmount());
        }

        if (null != orderDto.getAmount()) {
            MtOrder.setAmount(orderDto.getAmount());
        }

        if (null != orderDto.getDiscount()) {
            MtOrder.setDiscount(orderDto.getDiscount());
        }

        if (null != orderDto.getPayTime()) {
            MtOrder.setPayTime(orderDto.getPayTime());
        }

        if (null != orderDto.getPayStatus()) {
            MtOrder.setPayStatus(orderDto.getPayStatus());
        }

        if (null != orderDto.getExpressInfo()) {
            MtOrder.setExpressInfo(JSONObject.toJSONString(orderDto.getExpressInfo()));
        }

        if (null != orderDto.getRemark()) {
            MtOrder.setRemark(orderDto.getRemark());
        }

        return orderRepository.save(MtOrder);
    }

    @Override
    @Transactional
    @OperationServiceLog(description = "支付订单")
    public boolean setOrderPayed(Integer orderId) throws BusinessCheckException {
        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderId);
        reqDto.setStatus(OrderStatusEnum.PAID.getKey());
        reqDto.setPayStatus(PayStatusEnum.SUCCESS.getKey());
        reqDto.setPayTime(new Date());
        reqDto.setUpdateTime(new Date());
        this.updateOrder(reqDto);

        return true;
    }

    @Override
    public List<MtOrder> getOrderListByParams(Map<String, Object> params) {
        Specification<MtOrder> specification = orderRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        List<MtOrder> result = orderRepository.findAll(specification, sort);
        return result;
    }

    /**
     * 处理订单详情
     * @param orderInfo
     * @return UserOrderDto
     * */
    private UserOrderDto getOrderDetail(MtOrder orderInfo, boolean needAddress) throws BusinessCheckException {
        UserOrderDto dto = new UserOrderDto();

        dto.setId(orderInfo.getId());
        dto.setUserId(orderInfo.getUserId());
        dto.setCouponId(orderInfo.getCouponId());
        dto.setOrderSn(orderInfo.getOrderSn());
        dto.setRemark(orderInfo.getRemark());
        dto.setType(orderInfo.getType());
        dto.setPayType(orderInfo.getPayType());
        dto.setOrderMode(orderInfo.getOrderMode());
        dto.setCreateTime(DateUtil.formatDate(orderInfo.getCreateTime(), "yyyy.MM.dd HH:mm"));
        dto.setUpdateTime(DateUtil.formatDate(orderInfo.getUpdateTime(), "yyyy.MM.dd HH:mm"));
        dto.setAmount(orderInfo.getAmount());
        if (orderInfo.getPayAmount() != null) {
            dto.setPayAmount(orderInfo.getPayAmount());
        } else {
            dto.setPayAmount(new BigDecimal("0"));
        }
        if (orderInfo.getDiscount() != null) {
            dto.setDiscount(orderInfo.getDiscount());
        } else {
            dto.setDiscount(new BigDecimal("0"));
        }
        if (orderInfo.getPointAmount() != null) {
            dto.setPointAmount(orderInfo.getPointAmount());
        } else {
            dto.setPointAmount(new BigDecimal("0"));
        }
        dto.setStatus(orderInfo.getStatus());
        dto.setParam(orderInfo.getParam());
        dto.setPayStatus(orderInfo.getPayStatus());
        if (orderInfo.getUsePoint() != null) {
            dto.setUsePoint(orderInfo.getUsePoint());
        } else {
            dto.setUsePoint(0);
        }
        if (orderInfo.getPayTime() != null) {
            dto.setPayTime(DateUtil.formatDate(orderInfo.getPayTime(), "yyyy.MM.dd HH:mm"));
        }

        if (dto.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            dto.setTypeName(OrderTypeEnum.PRESTORE.getValue());
        } else if(dto.getType().equals(OrderTypeEnum.PAYMENT.getKey())) {
            dto.setTypeName(OrderTypeEnum.PAYMENT.getValue());
        } else if(dto.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
            dto.setTypeName(OrderTypeEnum.GOOGS.getValue());
        } else if(dto.getType().equals(OrderTypeEnum.MEMBER.getKey())) {
            dto.setTypeName(OrderTypeEnum.MEMBER.getValue());
        } else if(dto.getType().equals(OrderTypeEnum.RECHARGE.getKey())) {
            dto.setTypeName(OrderTypeEnum.RECHARGE.getValue());
        }

        if (dto.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
            dto.setStatusText(OrderStatusEnum.CREATED.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.CANCEL.getKey())) {
            dto.setStatusText(OrderStatusEnum.CANCEL.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.PAID.getKey())) {
            dto.setStatusText(OrderStatusEnum.PAID.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.DELIVERY.getKey())) {
            dto.setStatusText(OrderStatusEnum.DELIVERY.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.DELIVERED.getKey())) {
            dto.setStatusText(OrderStatusEnum.DELIVERED.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.RECEIVED.getKey())) {
            dto.setStatusText(OrderStatusEnum.RECEIVED.getValue());
        } else if(dto.getStatus().equals(OrderStatusEnum.DELETED.getKey())) {
            dto.setStatusText(OrderStatusEnum.DELETED.getValue());
        }

        // 订单所属店铺
        MtStore storeInfo = storeService.queryStoreById(orderInfo.getStoreId());
        dto.setStoreInfo(storeInfo);

        // 下单用户信息直接取会员个人信息
        OrderUserDto userInfo = new OrderUserDto();
        MtUser user = memberService.queryMemberById(orderInfo.getUserId());
        if (user != null) {
            userInfo.setId(user.getId());
            userInfo.setName(user.getName());
            userInfo.setMobile(user.getMobile());
            userInfo.setCardNo(user.getCarNo());
            userInfo.setAddress(user.getAddress());
            dto.setUserInfo(userInfo);
        }

        List<OrderGoodsDto> goodsList = new ArrayList<>();

        String baseImage = settingService.getUploadBasePath();

        // 预存卡的订单
        if (orderInfo.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
            MtCoupon coupon = couponService.queryCouponById(orderInfo.getCouponId());
            String[] paramArr = orderInfo.getParam().split(",");
            for(int i = 0; i < paramArr.length; i++) {
                String[] item = paramArr[i].split("_");
                OrderGoodsDto o = new OrderGoodsDto();
                o.setId(coupon.getId());
                o.setType(OrderTypeEnum.PRESTORE.getKey());
                o.setName("预存￥"+item[0]+"升至￥"+item[1]);
                o.setNum(Integer.parseInt(item[2]));
                o.setPrice(item[0]);
                o.setDiscount("0");
                if (coupon.getImage().indexOf(baseImage) == -1) {
                    o.setImage(baseImage + coupon.getImage());
                }
                goodsList.add(o);
            }
        }

        // 商品订单
        if (orderInfo.getType().equals(OrderTypeEnum.GOOGS.getKey())) {
            Map<String, Object> params = new HashMap<>();
            params.put("EQ_orderId", orderInfo.getId().toString());
            Specification<MtOrderGoods> specification = orderGoodsRepository.buildSpecification(params);
            Sort sort = new Sort(Sort.Direction.ASC, "createTime");
            List<MtOrderGoods> orderGoodsList = orderGoodsRepository.findAll(specification, sort);
            for (MtOrderGoods orderGoods : orderGoodsList) {
                MtGoods goodsInfo = goodsRepository.findOne(orderGoods.getGoodsId());
                if (goodsInfo != null) {
                    OrderGoodsDto o = new OrderGoodsDto();
                    o.setId(orderGoods.getId());
                    o.setName(goodsInfo.getName());
                    if (goodsInfo.getLogo().indexOf(baseImage) == -1) {
                        o.setImage(baseImage + goodsInfo.getLogo());
                    }

                    o.setType(OrderTypeEnum.GOOGS.getKey());
                    o.setNum(orderGoods.getNum());
                    o.setSkuId(orderGoods.getSkuId());
                    o.setPrice(orderGoods.getPrice().toString());
                    o.setDiscount(orderGoods.getDiscount().toString());
                    o.setGoodsId(orderGoods.getGoodsId());

                    if (orderGoods.getSkuId() > 0) {
                        List<GoodsSpecValueDto> specList = goodsService.getSpecListBySkuId(orderGoods.getSkuId());
                        o.setSpecList(specList);
                    }
                    goodsList.add(o);
                }
            }
        }

        // 配送地址
        if (orderInfo.getOrderMode().equals(OrderModeEnum.EXPRESS.getKey()) && needAddress) {
            List<MtOrderAddress> orderAddressList = orderAddressRepository.getOrderAddress(orderInfo.getId());
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
                    MtRegion mtProvince = regionRepository.findOne(orderAddress.getProvinceId());
                    if (mtProvince != null) {
                        address.setProvinceName(mtProvince.getName());
                    }
                }
                if (orderAddress.getCityId() > 0) {
                    MtRegion mtCity = regionRepository.findOne(orderAddress.getCityId());
                    if (mtCity != null) {
                        address.setCityName(mtCity.getName());
                    }
                }
                if (orderAddress.getRegionId() > 0) {
                    MtRegion mtRegion = regionRepository.findOne(orderAddress.getRegionId());
                    if (mtRegion != null) {
                        address.setRegionName(mtRegion.getName());
                    }
                }

                dto.setAddress(address);
            }
        }

        // 物流信息
        if (StringUtil.isNotEmpty(orderInfo.getExpressInfo())) {
            JSONObject express = JSONObject.parseObject(orderInfo.getExpressInfo());
            ExpressDto expressInfo = new ExpressDto();
            expressInfo.setExpressNo(express.get("expressNo").toString());
            expressInfo.setExpressCompany(express.get("expressCompany").toString());
            expressInfo.setExpressTime(express.get("expressTime").toString());
            dto.setExpressInfo(expressInfo);
        }

        dto.setGoods(goodsList);
        return dto;
    }

    /**
     * 获取订单数量
     * */
    @Override
    public BigDecimal getOrderCount(Integer storeId) throws BusinessCheckException {
        if (storeId > 0) {
            return orderRepository.getOrderCount(storeId);
        } else {
            return orderRepository.getOrderCount();
        }
    }

    /**
     * 获取订单数量
     * */
    @Override
    public BigDecimal getOrderCount(Integer storeId, Date beginTime, Date endTime) throws BusinessCheckException {
        if (storeId > 0) {
            return orderRepository.getOrderCount(storeId, beginTime, endTime);
        } else {
            return orderRepository.getOrderCount(beginTime, endTime);
        }
    }

    /**
     * 获取支付金额
     * */
    @Override
    public BigDecimal getPayMoney(Integer storeId, Date beginTime, Date endTime) {
        if (storeId > 0) {
            return orderRepository.getPayMoney(storeId, beginTime, endTime);
        } else {
            return orderRepository.getPayMoney(beginTime, endTime);
        }
    }

    /**
     * 获取支付人数
     * */
    @Override
    public Integer getPayUserCount(Integer storeId) {
        if (storeId > 0) {
            return orderRepository.getPayUserCount(storeId);
        } else {
            return orderRepository.getPayUserCount();
        }
    }

    /**
     * 获取支付总金额
     * */
    @Override
    public BigDecimal getPayMoney(Integer storeId) {
        if (storeId > 0) {
            return orderRepository.getPayMoney(storeId);
        } else {
            return orderRepository.getPayMoney();
        }
    }

    /**
     * 计算商品总价
     * @param userId
     * @param cartList
     * @param couponId
     * @param isUsePoint
     * @return
     * */
    @Override
    public Map<String, Object> calculateCartGoods(Integer userId, List<MtCart> cartList, Integer couponId, boolean isUsePoint) throws BusinessCheckException {
        MtUser userInfo = memberService.queryMemberById(userId);

        // 设置是否不能用积分抵扣
        MtSetting pointSetting = settingService.querySettingByName(PointSettingEnum.CAN_USE_AS_MONEY.getKey());
        if (pointSetting != null && !pointSetting.getValue().equals("true")) {
            isUsePoint = false;
        }

        List<ResCartDto> cartDtoList = new ArrayList<>();
        String basePath = settingService.getUploadBasePath();
        Integer totalNum = 0;
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal totalCanUsePointAmount = new BigDecimal("0");

        if (cartList.size() > 0) {
            for (MtCart cart : cartList) {
                // 购物车商品信息
                MtGoods mtGoodsInfo = goodsService.queryGoodsById(cart.getGoodsId());
                if (mtGoodsInfo == null || !mtGoodsInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    continue;
                }

                totalNum = totalNum + cart.getNum();
                ResCartDto cartDto = new ResCartDto();
                cartDto.setId(cart.getId());
                cartDto.setGoodsId(cart.getGoodsId());
                cartDto.setNum(cart.getNum());
                cartDto.setSkuId(cart.getSkuId());
                cartDto.setUserId(cart.getUserId());

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
                    MtGoodsSku mtGoodsSku = goodsSkuRepository.findOne(cart.getSkuId());
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

                // 计算总价
                totalPrice = totalPrice.add(cartDto.getGoodsInfo().getPrice().multiply(new BigDecimal(cart.getNum())));

                // 累加可用积分去抵扣的金额
                if (mtGoodsInfo.getCanUsePoint().equals("Y")) {
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
                // 优惠券和预存券才能使用
                if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey()) || couponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                    CouponDto couponDto = new CouponDto();
                    couponDto.setId(couponInfo.getId());
                    couponDto.setUserCouponId(userCoupon.getId());
                    couponDto.setName(couponInfo.getName());
                    couponDto.setAmount(userCoupon.getAmount());
                    couponDto.setStatus(UserCouponStatusEnum.DISABLE.getKey());

                    boolean isEffective = couponService.isCouponEffective(couponInfo);

                    // 优惠券
                    if (couponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                        couponDto.setType(CouponTypeEnum.COUPON.getValue());
                        if (StringUtil.isEmpty(couponInfo.getOutRule())) {
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
                        // 预存卡
                        couponDto.setType(CouponTypeEnum.PRESTORE.getValue());
                        couponDto.setDescription("无使用门槛");
                        couponDto.setAmount(userCoupon.getAmount());
                        // 余额须大于0
                        if (isEffective && (userCoupon.getBalance().compareTo(new BigDecimal("0")) > 0)) {
                            couponDto.setStatus(UserCouponStatusEnum.UNUSED.getKey());
                        }
                    }
                    couponDto.setEffectiveDate(couponInfo.getBeginTime() + "~" + couponInfo.getEndTime());
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
                boolean isEffective = couponService.isCouponEffective(useCouponInfo);
                if (isEffective) {
                   if (useCouponInfo.getType().equals(CouponTypeEnum.COUPON.getKey())) {
                       couponAmount = useCouponInfo.getAmount();
                   } else if(useCouponInfo.getType().equals(CouponTypeEnum.PRESTORE.getKey())) {
                       BigDecimal couponTotalAmount = userCouponInfo.getAmount();
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
        MtSetting setting = settingService.querySettingByName(PointSettingEnum.EXCHANGE_NEED_POINT.getKey());
        if (myPoint > 0 && setting != null && isUsePoint) {
            if (StringUtil.isNotEmpty(setting.getValue())) {
                BigDecimal usePoints = new BigDecimal(myPoint);
                usePointAmount = usePoints.divide(new BigDecimal(setting.getValue()));
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

        return result;
    }
}
