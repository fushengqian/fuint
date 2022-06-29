package com.fuint.application.service.refund;

import com.fuint.application.BaseService;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.*;
import com.fuint.application.dao.repositories.MtConfirmLogRepository;
import com.fuint.application.dao.repositories.MtRefundRepository;
import com.fuint.application.dao.repositories.MtUserCouponRepository;
import com.fuint.application.dto.*;
import com.fuint.application.enums.*;
import com.fuint.application.service.coupon.CouponService;
import com.fuint.application.service.order.OrderService;
import com.fuint.application.service.point.PointService;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 售后接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class RefundServiceImpl extends BaseService implements RefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundServiceImpl.class);

    @Autowired
    private MtRefundRepository refundRepository;

    @Autowired
    private MtConfirmLogRepository confirmLogRepository;

    @Autowired
    private MtUserCouponRepository userCouponRepository;

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
     * 分页查询售后订单列表
     *
     * @param  paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtRefund> getRefundListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtRefund> paginationResponse = refundRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 获取用户售后订单列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject getUserRefundList(Map<String, Object> paramMap) throws BusinessCheckException {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "0" : paramMap.get("userId").toString();
        String status =  paramMap.get("status") == null ? "": paramMap.get("status").toString();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_status", status);
        searchParams.put("EQ_userId", userId);

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"updateTime desc", "status asc"});
        PaginationResponse<MtRefund> paginationResponse = refundRepository.findResultsByPagination(paginationRequest);

        return getSuccessResult(paginationResponse);
    }

    /**
     * 创建售后订单
     *
     * @param refundDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "创建售后订单")
    public MtRefund createRefund(RefundDto refundDto) throws BusinessCheckException {
        MtRefund refund = new MtRefund();
        if (refundDto.getId() != null) {
            refund.setId(refund.getId());
        }

        // 检查是否已存在
        Map<String, Object> params = new HashMap<>();
        params.put("EQ_userId", refundDto.getUserId().toString());
        params.put("EQ_orderId", refundDto.getOrderId().toString());
        Specification<MtRefund> specification = refundRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        List<MtRefund> result = refundRepository.findAll(specification, sort);

        if (result.size() > 0) {
            refund = result.get(0);
            refund.setUpdateTime(new Date());
            return refundRepository.save(refund);
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

        return refundRepository.save(refund);
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @throws BusinessCheckException
     */
    @Override
    public MtRefund getRefundById(Integer id) {
        return refundRepository.findOne(id);
    }

    /**
     * 修改订单
     *
     * @param refundDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改售后订单")
    public MtRefund updateRefund(RefundDto refundDto) throws BusinessCheckException {
        MtRefund refund = refundRepository.findOne(refundDto.getId());
        if (null == refund) {
            log.error("该售后订单状态异常");
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

        return refundRepository.save(refund);
    }

    /**
     * 同意售后订单
     * @param refundDto
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    @OperationServiceLog(description = "同意售后订单")
    public MtRefund agreeRefund(RefundDto refundDto) throws BusinessCheckException {
        MtRefund refund = refundRepository.findOne(refundDto.getId());
        if (null == refund) {
            log.error("该售后订单状态异常");
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

        MtRefund mtRefund = refundRepository.save(refund);
        UserOrderDto orderInfo = orderService.getOrderById(mtRefund.getOrderId());

        OrderDto reqDto = new OrderDto();
        reqDto.setId(orderInfo.getId());
        reqDto.setStatus(OrderStatusEnum.CANCEL.getKey());
        orderService.updateOrder(reqDto);

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
        List<MtConfirmLog> confirmLogList = confirmLogRepository.getOrderConfirmLogList(orderInfo.getId());
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

        return mtRefund;
    }

    /**
     * 获取售后订单数量
     * */
    @Override
    public Long getRefundCount(Date beginTime, Date endTime) {
        return refundRepository.getRefundCount(beginTime, endTime);
    }
}
