package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.Constants;
import com.fuint.common.dto.*;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.common.util.DateUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.*;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 售后接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class RefundServiceImpl extends ServiceImpl<MtRefundMapper, MtRefund> implements RefundService {

    private static final Logger logger = LoggerFactory.getLogger(RefundServiceImpl.class);

    @Resource
    private MtPointMapper mtPointMapper;

    @Resource
    private MtRefundMapper mtRefundMapper;

    @Resource
    private MtConfirmLogMapper mtConfirmLogMapper;

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

    @Resource
    private MtGoodsSkuMapper mtGoodsSkuMapper;

    @Resource
    private MtGoodsMapper mtGoodsMapper;

    @Resource
    private MtOrderGoodsMapper mtOrderGoodsMapper;

    /**
     * 卡券接口
     * */
    @Autowired
    private CouponService couponService;

    /**
     * 积分相关接口
     * */
    @Autowired
    private PointService pointService;

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
     * 微信服务接口
     * */
    @Autowired
    private WeixinService weixinService;

    /**
     * 支付宝服务接口
     * */
    @Autowired
    private AlipayService alipayService;

    /**
     * 分页查询售后订单列表
     *
     * @param  paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtRefund> getRefundListByPagination(PaginationRequest paginationRequest) {
        Page<MtBanner> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtRefund> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtRefund::getStatus, StatusEnum.DISABLE.getKey());

        String remark = paginationRequest.getSearchParams().get("remark") == null ? "" : paginationRequest.getSearchParams().get("remark").toString();
        if (StringUtils.isNotBlank(remark)) {
            lambdaQueryWrapper.like(MtRefund::getRemark, remark);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtRefund::getStatus, status);
        }
        String orderId = paginationRequest.getSearchParams().get("orderId") == null ? "" : paginationRequest.getSearchParams().get("orderId").toString();
        if (StringUtils.isNotBlank(orderId)) {
            lambdaQueryWrapper.eq(MtRefund::getOrderId, orderId);
        }
        String userId = paginationRequest.getSearchParams().get("userId") == null ? "" : paginationRequest.getSearchParams().get("userId").toString();
        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.eq(MtRefund::getUserId, userId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtRefund::getStoreId, storeId);
        }
        String startTime = paginationRequest.getSearchParams().get("startTime") == null ? "" : paginationRequest.getSearchParams().get("startTime").toString();
        String endTime = paginationRequest.getSearchParams().get("endTime") == null ? "" : paginationRequest.getSearchParams().get("endTime").toString();
        if (StringUtil.isNotEmpty(startTime)) {
            lambdaQueryWrapper.ge(MtRefund::getCreateTime, startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            lambdaQueryWrapper.le(MtRefund::getCreateTime, endTime);
        }
        lambdaQueryWrapper.orderByDesc(MtRefund::getId);
        List<MtRefund> dataList = mtRefundMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtRefund> paginationResponse = new PaginationResponse(pageImpl, MtRefund.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 获取用户售后订单列表
     *
     * @param  paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseObject getUserRefundList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "0" : paramMap.get("userId").toString();
        String status =  paramMap.get("status") == null ? "": paramMap.get("status").toString();

        Page<MtBanner> pageHelper = PageHelper.startPage(pageNumber, pageSize);
        LambdaQueryWrapper<MtRefund> lambdaQueryWrapper = Wrappers.lambdaQuery();

        if (StringUtils.isNotBlank(userId)) {
            lambdaQueryWrapper.like(MtRefund::getUserId, userId);
        }
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtRefund::getStatus, status);
        }

        lambdaQueryWrapper.orderByDesc(MtRefund::getId);
        List<MtRefund> refundList = mtRefundMapper.selectList(lambdaQueryWrapper);

        List<RefundDto> dataList = new ArrayList<>();
        if (refundList != null && refundList.size() > 0) {
            for (MtRefund mtRefund : refundList) {
                 RefundDto refundDto = new RefundDto();
                 BeanUtils.copyProperties(mtRefund, refundDto);
                 UserOrderDto orderDto = orderService.getOrderById(mtRefund.getOrderId());
                 if (mtRefund.getImages() != null && StringUtil.isNotEmpty(mtRefund.getImages())) {
                     List<String> images = Arrays.asList(mtRefund.getImages().split(",").clone());
                     refundDto.setImageList(images);
                 }
                 refundDto.setOrderInfo(orderDto);
                 refundDto.setCreateTime(DateUtil.formatDate(mtRefund.getCreateTime(), "yyyy.MM.dd HH:mm"));
                 refundDto.setUpdateTime(DateUtil.formatDate(mtRefund.getUpdateTime(), "yyyy.MM.dd HH:mm"));

                 if (mtRefund.getStatus().equals(RefundStatusEnum.CREATED.getKey())) {
                     refundDto.setStatusText(RefundStatusEnum.CREATED.getValue());
                 }
                 if (mtRefund.getStatus().equals(RefundStatusEnum.APPROVED.getKey())) {
                     refundDto.setStatusText(RefundStatusEnum.APPROVED.getValue());
                 }
                 if (mtRefund.getStatus().equals(RefundStatusEnum.REJECT.getKey())) {
                     refundDto.setStatusText(RefundStatusEnum.REJECT.getValue());
                 }
                 if (mtRefund.getStatus().equals(RefundStatusEnum.CANCEL.getKey())) {
                     refundDto.setStatusText(RefundStatusEnum.CANCEL.getValue());
                 }

                 dataList.add(refundDto);
            }
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<RefundDto> paginationResponse = new PaginationResponse(pageImpl, RefundDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return new ResponseObject(200, "查询成功", paginationResponse);
    }

    /**
     * 创建售后订单
     *
     * @param refundDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "提交售后订单")
    public MtRefund createRefund(RefundDto refundDto) {
        MtRefund refund = new MtRefund();
        if (refundDto.getId() != null) {
            refund.setId(refund.getId());
        }

        // 检查是否已存在
        Map<String, Object> params = new HashMap<>();
        params.put("USER_ID", refundDto.getUserId().toString());
        params.put("ORDER_ID", refundDto.getOrderId().toString());
        List<MtRefund> result = mtRefundMapper.selectByMap(params);

        if (result.size() > 0) {
            refund = result.get(0);
            refund.setUpdateTime(new Date());
            if (refundDto.getRemark() != null && StringUtil.isNotEmpty(refundDto.getRemark())) {
                refund.setRemark(refund.getRemark() + "|" + refundDto.getRemark());
            }
            mtRefundMapper.updateById(refund);
            return refund;
        }

        refund.setOrderId(refundDto.getOrderId());
        refund.setUserId(refundDto.getUserId());
        refund.setRemark(refundDto.getRemark());
        refund.setType(refundDto.getType());
        refund.setStoreId(refundDto.getStoreId());
        refund.setAmount(refundDto.getAmount());
        if (refundDto.getImages() != null && StringUtil.isNotEmpty(refundDto.getImages()) && refundDto.getImages().length() > 5) {
            refund.setImages(String.join(",", refundDto.getImages()));
        }
        refund.setStatus(RefundStatusEnum.CREATED.getKey());
        refund.setUpdateTime(new Date());
        refund.setCreateTime(new Date());

        mtRefundMapper.insert(refund);
        return refund;
    }

    /**
     * 根据ID获取订单详情
     *
     * @param  id 售后订单ID
     * @throws BusinessCheckException
     */
    @Override
    public RefundDto getRefundById(Integer id) throws BusinessCheckException {
        MtRefund mtRefund = mtRefundMapper.selectById(id);
        if (mtRefund != null) {
            RefundDto refundDto = new RefundDto();
            BeanUtils.copyProperties(mtRefund, refundDto);
            UserOrderDto orderDto = orderService.getOrderById(mtRefund.getOrderId());
            if (mtRefund.getImages() != null && StringUtil.isNotEmpty(mtRefund.getImages())) {
                List<String> images = Arrays.asList(mtRefund.getImages().split(",").clone());
                refundDto.setImageList(images);
            }
            refundDto.setOrderInfo(orderDto);
            AddressDto address = new AddressDto();
            refundDto.setAddress(address);
            return refundDto;
        }
        return null;
    }

    /**
     * 根据订单ID获取售后订单信息
     *
     * @param  orderId
     * @throws BusinessCheckException
     */
    @Override
    public MtRefund getRefundByOrderId(Integer orderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ORDER_ID", orderId.toString());
        List<MtRefund> refunds = mtRefundMapper.selectByMap(params);
        if (refunds != null && refunds.size() > 0) {
            return refunds.get(0);
        }
        return null;
    }

    /**
     * 修改订单
     *
     * @param  refundDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "更新售后订单")
    public MtRefund updateRefund(RefundDto refundDto) throws BusinessCheckException {
        MtRefund refund = mtRefundMapper.selectById(refundDto.getId());
        if (refund == null) {
            throw new BusinessCheckException("该售后订单状态异常");
        }

        refund.setId(refundDto.getId());
        refund.setUpdateTime(new Date());

        if (null != refundDto.getOperator()) {
            refund.setOperator(refundDto.getOperator());
        }
        if (null != refundDto.getStatus()) {
            refund.setStatus(refundDto.getStatus());
        }

        mtRefundMapper.updateById(refund);
        return refund;
    }

    /**
     * 同意售后订单
     * @param refundDto
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MtRefund agreeRefund(RefundDto refundDto) throws BusinessCheckException {
        MtRefund refund = mtRefundMapper.selectById(refundDto.getId());
        if (null == refund) {
            throw new BusinessCheckException("该售后订单状态异常");
        }

        // 已经同意过了
        if (refund.getStatus().equals(RefundStatusEnum.APPROVED.getKey())) {
            if (StringUtil.isNotEmpty(refundDto.getRemark())) {
                refund.setRemark(refundDto.getRemark());
            }
            mtRefundMapper.updateById(refund);
            return refund;
        }

        refund.setId(refundDto.getId());
        refund.setUpdateTime(new Date());

        if (null != refundDto.getOperator()) {
            refund.setOperator(refundDto.getOperator());
        }

        if (null != refundDto.getStatus()) {
            refund.setStatus(refundDto.getStatus());
        }

        mtRefundMapper.updateById(refund);
        MtRefund mtRefund = mtRefundMapper.selectById(refund.getId());
        UserOrderDto orderInfo = orderService.getOrderById(mtRefund.getOrderId());

        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderInfo.getId());
        reqDto.setStatus(OrderStatusEnum.REFUND.getKey());
        orderService.updateOrder(reqDto);

        // 换货
        if (refund.getType().equals(RefundTypeEnum.EXCHANGE.getKey())) {
            return refund;
        }

        // 如果是余额支付，返还余额
        if (orderInfo.getPayType().equals(PayTypeEnum.BALANCE.getKey())) {
            List<MtBalance> balanceList = balanceService.getBalanceListByOrderSn(orderInfo.getOrderSn());
            if (balanceList.size() > 0) {
               for (MtBalance mtBalance : balanceList) {
                   if (mtBalance.getAmount().compareTo(new BigDecimal("0")) < 0) {
                       MtBalance balanceReq = new MtBalance();
                       balanceReq.setUserId(orderInfo.getUserId());
                       balanceReq.setOrderSn(orderInfo.getOrderSn());
                       balanceReq.setMobile(orderInfo.getUserInfo().getMobile());
                       balanceReq.setAmount(mtBalance.getAmount().negate());
                       balanceReq.setStatus(StatusEnum.ENABLED.getKey());
                       balanceReq.setCreateTime(new Date());
                       balanceReq.setUpdateTime(new Date());
                       balanceService.addBalance(balanceReq);
                   }
               }
            }
        }

        // 返还积分
        if (orderInfo.getUsePoint() != null && orderInfo.getUsePoint() > 0) {
            MtPoint reqPointDto = new MtPoint();
            reqPointDto.setUserId(orderInfo.getUserId());
            reqPointDto.setAmount(orderInfo.getUsePoint());
            reqPointDto.setDescription("售后订单" + orderInfo.getOrderSn() + "退回"+ orderInfo.getUsePoint() +"积分");
            reqPointDto.setOrderSn(orderInfo.getOrderSn());
            reqPointDto.setOperator("");
            pointService.addPoint(reqPointDto);
        }

        // 返还卡券
        List<MtConfirmLog> confirmLogList = mtConfirmLogMapper.getOrderConfirmLogList(orderInfo.getId());
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

        // 退回积分
        Map<String, Object> params = new HashMap<>();
        params.put("USER_ID", orderInfo.getUserId());
        params.put("ORDER_SN", orderInfo.getOrderSn());
        List<MtPoint> pointList = mtPointMapper.selectByMap(params);
        if (pointList != null && pointList.size() > 0) {
            Integer pointNum = pointList.get(0).getAmount();
            if (pointNum > 0) {
                Integer amount = pointNum - (pointNum) * 2;
                MtPoint mtPoint = new MtPoint();
                mtPoint.setAmount(amount.intValue());
                mtPoint.setUserId(orderInfo.getUserId());
                mtPoint.setOrderSn(orderInfo.getOrderSn());
                mtPoint.setDescription("退款￥" + orderInfo.getPayAmount() + "退回" + pointNum + "积分");
                mtPoint.setOperator(refundDto.getOperator() == null ? "系统" : refundDto.getOperator());
                pointService.addPoint(mtPoint);
            }
        }

        // 返还库存
        Map<String, Object> eParam = new HashMap<>();
        eParam.put("ORDER_ID", orderInfo.getId());
        List<MtOrderGoods> orderGoodsList = mtOrderGoodsMapper.selectByMap(eParam);
        if (orderGoodsList != null && orderGoodsList.size() > 0) {
            for (MtOrderGoods mtOrderGoods : orderGoodsList) {
                MtGoods mtGoods = mtGoodsMapper.selectById(mtOrderGoods.getGoodsId());
                mtGoods.setStock(mtOrderGoods.getNum() + mtGoods.getStock());
                mtGoodsMapper.updateById(mtGoods);
                if (mtOrderGoods.getSkuId() != null && mtOrderGoods.getSkuId() > 0) {
                    MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(mtOrderGoods.getSkuId());
                    mtGoodsSku.setStock(mtGoodsSku.getStock() + mtOrderGoods.getNum());
                    mtGoodsSkuMapper.updateById(mtGoodsSku);
                }
            }
        }

        // 微信支付发起退款
        if (orderInfo.getPayType().equals(PayTypeEnum.JSAPI.getKey()) || orderInfo.getPayType().equals(PayTypeEnum.MICROPAY.getKey())) {
            weixinService.doRefund(orderInfo.getStoreInfo() != null ? orderInfo.getStoreInfo().getId() : 0, orderInfo.getOrderSn(), orderInfo.getPayAmount(), mtRefund.getAmount(), PlatformTypeEnum.MP_WEIXIN.getCode());
        }

        // 支付宝发起退款
        if (orderInfo.getPayType().equals(PayTypeEnum.ALISCAN.getKey())) {
            alipayService.doRefund(orderInfo.getStoreInfo() != null ? orderInfo.getStoreInfo().getId() : 0, orderInfo.getOrderSn(), orderInfo.getPayAmount(), mtRefund.getAmount(), PlatformTypeEnum.PC.getCode());
        }

        return mtRefund;
    }

    /**
     * 发起退款
     * @param orderId
     * @param refundAmount
     * @param remark
     * @param accountInfo
     * throws BusinessCheckException;
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean doRefund(Integer orderId, String refundAmount, String remark, AccountInfo accountInfo) throws BusinessCheckException {
        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        if (orderInfo == null) {
            logger.error("退款订单为空，orderId = " + orderId + orderInfo.getId());
            throw new BusinessCheckException("该订单状态异常！");
        }

        MtRefund refund = mtRefundMapper.findByOrderId(orderId);
        if (refund != null) {
            logger.error("售后订单已存在，orderId = " + orderId);
            throw new BusinessCheckException("该售后订单已存在，请查询售后订单列表！");
        }

        if (new BigDecimal(refundAmount).compareTo(orderInfo.getPayAmount()) > 0) {
            throw new BusinessCheckException("退款金额不能大于实际支付金额！");
        }

        // 创建售后订单
        RefundDto refundDto = new RefundDto();
        refundDto.setUserId(orderInfo.getUserId());
        refundDto.setOrderId(orderInfo.getId());
        refundDto.setRemark(remark);
        refundDto.setType(RefundTypeEnum.RETURN.getKey());
        if (orderInfo.getStoreInfo() != null) {
            refundDto.setStoreId(orderInfo.getStoreInfo().getId());
        }
        refundDto.setAmount(new BigDecimal(refundAmount));
        refundDto.setOperator(accountInfo.getAccountName());
        refundDto.setImages(null);
        MtRefund mtRefund = createRefund(refundDto);
        if (mtRefund != null) {
            // 审核同意
            RefundDto agreeDto = new RefundDto();
            agreeDto.setId(mtRefund.getId());
            agreeDto.setOperator(accountInfo.getAccountName());
            agreeDto.setStatus(RefundStatusEnum.APPROVED.getKey());
            MtRefund refundInfo = agreeRefund(agreeDto);
            if (refundInfo == null) {
                logger.error("退款审核失败，orderId = " + orderId + ", refundId = " + mtRefund.getId());
                throw new BusinessCheckException("退款审核失败！");
            }
        } else {
            logger.error("退款生成售后订单失败，orderId = " + orderId);
            throw new BusinessCheckException("生成售后订单失败！");
        }
        return true;
    }

    /**
     * 获取售后订单数量
     * */
    @Override
    public Long getRefundCount(Date beginTime, Date endTime) {
        return mtRefundMapper.getRefundCount(beginTime, endTime);
    }
}
