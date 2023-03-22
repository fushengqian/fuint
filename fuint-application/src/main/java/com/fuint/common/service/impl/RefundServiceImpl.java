package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.Constants;
import com.fuint.common.dto.OrderDto;
import com.fuint.common.dto.RefundDto;
import com.fuint.common.dto.UserOrderDto;
import com.fuint.common.enums.*;
import com.fuint.common.service.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.mapper.MtConfirmLogMapper;
import com.fuint.repository.mapper.MtRefundMapper;
import com.fuint.repository.mapper.MtUserCouponMapper;
import com.fuint.repository.model.*;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class RefundServiceImpl extends ServiceImpl<MtRefundMapper, MtRefund> implements RefundService {

    @Resource
    private MtRefundMapper mtRefundMapper;

    @Resource
    private MtConfirmLogMapper mtConfirmLogMapper;

    @Resource
    private MtUserCouponMapper mtUserCouponMapper;

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
    @Transactional
    public ResponseObject getUserRefundList(Map<String, Object> paramMap) {
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
        List<MtRefund> dataList = mtRefundMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtRefund> paginationResponse = new PaginationResponse(pageImpl, MtRefund.class);
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
    @Transactional
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
            mtRefundMapper.updateById(refund);
            return refund;
        }

        refund.setOrderId(refundDto.getOrderId());
        refund.setUserId(refundDto.getUserId());
        refund.setRemark(refundDto.getRemark());
        refund.setType(refundDto.getType());
        refund.setStoreId(refundDto.getStoreId());
        refund.setAmount(refundDto.getAmount());
        refund.setImages(refundDto.getImages());
        refund.setStatus(RefundStatusEnum.CREATED.getKey());
        refund.setUpdateTime(new Date());
        refund.setCreateTime(new Date());

        mtRefundMapper.insert(refund);
        return refund;
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @throws BusinessCheckException
     */
    @Override
    public MtRefund getRefundById(Integer id) {
        return mtRefundMapper.selectById(id);
    }

    /**
     * 修改订单
     *
     * @param refundDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
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
    @Transactional
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
        reqDto.setStatus(OrderStatusEnum.CANCEL.getKey());
        orderService.updateOrder(reqDto);

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

        return mtRefund;
    }

    /**
     * 获取售后订单数量
     * */
    @Override
    public Long getRefundCount(Date beginTime, Date endTime) {
        return mtRefundMapper.getRefundCount(beginTime, endTime);
    }
}
