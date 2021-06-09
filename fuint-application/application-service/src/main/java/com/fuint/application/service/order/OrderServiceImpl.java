package com.fuint.application.service.order;

import com.fuint.application.BaseService;
import com.fuint.application.ResponseObject;
import com.fuint.application.config.Constants;
import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dao.entities.MtUserCoupon;
import com.fuint.application.dao.repositories.MtOrderRepository;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.dto.OrderGoodsDto;
import com.fuint.application.dto.UserOrderDto;
import com.fuint.application.dto.ResUserOrderDto;
import com.fuint.application.enums.CouponTypeEnum;
import com.fuint.application.enums.OrderTypeEnum;
import com.fuint.application.util.CommonUtil;
import com.fuint.application.util.DateUtil;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.exception.BusinessRuntimeException;
import com.fuint.application.enums.StatusEnum;
import com.fuint.application.enums.OrderStatusEnum;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单接口实现类
 * Created by zach 2021/05/05
 */
@Service
public class OrderServiceImpl extends BaseService implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private MtOrderRepository orderRepository;

    /**
     * 分页查询订单列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtOrder> getOrderListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException {
        PaginationResponse<MtOrder> paginationResponse = orderRepository.findResultsByPagination(paginationRequest);
        return paginationResponse;
    }

    /**
     * 获取用户订单列表
     * @param paramMap
     * @throws BusinessCheckException
     * */
    @Override
    @Transactional
    public ResponseObject getUserOrderList(Map<String, Object> paramMap) {
        Integer pageNumber = paramMap.get("pageNumber") == null ? Constants.PAGE_NUMBER : Integer.parseInt(paramMap.get("pageNumber").toString());
        Integer pageSize = paramMap.get("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(paramMap.get("pageSize").toString());
        String userId = paramMap.get("userId") == null ? "0" : paramMap.get("userId").toString();
        String status =  paramMap.get("status") == null ? "": paramMap.get("status").toString();
        String dataType =  paramMap.get("dataType") == null ? "": paramMap.get("dataType").toString();

        if (dataType.equals("pay")) {
            status = OrderStatusEnum.CREATED.getKey();
        } else if(dataType.equals("completed")) {
            status = OrderStatusEnum.PAID.getKey();
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_status", status);
        searchParams.put("EQ_userId", userId);

        paginationRequest.setSearchParams(searchParams);
        paginationRequest.setSortColumn(new String[]{"id desc", "status asc"});
        PaginationResponse<MtOrder> paginationResponse = orderRepository.findResultsByPagination(paginationRequest);

        List<UserOrderDto> dataList = new ArrayList<>();
        if (paginationResponse.getContent().size() > 0) {
            for (MtOrder order : paginationResponse.getContent()) {
                UserOrderDto dto = new UserOrderDto();
                dto.setId(order.getId());
                dto.setCouponId(order.getCouponId());
                dto.setOrderSn(order.getOrderSn());

                String createTime = DateUtil.formatDate(order.getCreateTime(), "yyyy.MM.dd HH:mm");
                dto.setCreateTime(createTime);
                dto.setAmount(order.getAmount());

                dto.setStatus(order.getStatus());

                if (dto.getStatus().equals(OrderStatusEnum.CREATED.getKey())) {
                    dto.setStatusText(OrderStatusEnum.CREATED.getValue());
                } else if(dto.getStatus().equals(OrderStatusEnum.CANCEL.getKey())) {
                    dto.setStatusText(OrderStatusEnum.CANCEL.getValue());
                } else if(dto.getStatus().equals(OrderStatusEnum.PAID.getKey())) {
                    dto.setStatusText(OrderStatusEnum.PAID.getValue());
                }

                List<OrderGoodsDto> goodsList = new ArrayList<>();

                // 预存卡的订单
                if (order.getType().equals(OrderTypeEnum.PRESTORE.getKey())) {
                    String[] paramArr = order.getParam().split(",");
                    for(int i = 0; i < paramArr.length; i++) {
                        String[] item = paramArr[i].split("_");
                        OrderGoodsDto o = new OrderGoodsDto();
                        o.setName("预存￥"+item[0]+"升至￥"+item[1]);
                        o.setNum(Integer.parseInt(item[2]));
                        o.setPrice(item[0]);
                        goodsList.add(o);
                    }
                }

                dto.setGoods(goodsList);

                dataList.add(dto);
            }
        }

        ResUserOrderDto resUserOrderDto = new ResUserOrderDto();
        resUserOrderDto.setPageNumber(pageNumber);
        resUserOrderDto.setPageSize(pageSize);
        resUserOrderDto.getTotalRow(paginationResponse.getTotalElements());
        resUserOrderDto.setTotalPage(paginationResponse.getTotalPages());
        resUserOrderDto.setContent(dataList);

        return getSuccessResult(resUserOrderDto);
    }

    /**
     * 创建订单
     *
     * @param orderDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "创建订单")
    public MtOrder createOrder(OrderDto orderDto) throws BusinessCheckException {
        MtOrder MtOrder = new MtOrder();
        if (null != orderDto.getId()) {
            MtOrder.setId(MtOrder.getId());
        }

        String orderSn = CommonUtil.createOrderSN(orderDto.getUserId()+"");
        MtOrder.setOrderSn(orderSn);
        MtOrder.setUserId(orderDto.getUserId());
        MtOrder.setCouponId(orderDto.getCouponId());
        MtOrder.setParam(orderDto.getParam());
        MtOrder.setRemark(orderDto.getRemark());
        MtOrder.setStatus(OrderStatusEnum.CREATED.getKey());
        MtOrder.setType(orderDto.getType());
        MtOrder.setAmount(orderDto.getAmount());

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = format.format(new Date());
            Date addTime = format.parse(dt);
            MtOrder.setUpdateTime(addTime);
            MtOrder.setCreateTime(addTime);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("日期转换异常 " + e.getMessage());
        }

        return orderRepository.save(MtOrder);
    }

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @throws BusinessCheckException
     */
    @Override
    public MtOrder getOrderById(Integer id) throws BusinessCheckException {
        return orderRepository.findOne(id);
    }

    /**
     * 根据ID删除数据
     *
     * @param id       订单ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除订单")
    public void deleteOrder(Integer id, String operator) throws BusinessCheckException {
        MtOrder MtOrder = this.getOrderById(id);
        if (null == MtOrder) {
            return;
        }

        MtOrder.setStatus(StatusEnum.DISABLE.getKey());
        MtOrder.setUpdateTime(new Date());

        orderRepository.save(MtOrder);
    }

    /**
     * 修改订单
     *
     * @param orderDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "修改订单")
    public MtOrder updateOrder(OrderDto orderDto) throws BusinessCheckException {
        MtOrder MtOrder = this.getOrderById(orderDto.getId());
        if (null == MtOrder || StatusEnum.DISABLE.getKey().equals(MtOrder.getStatus())) {
            log.error("该订单状态异常");
            throw new BusinessCheckException("该订单状态异常");
        }

        MtOrder.setId(orderDto.getId());
        MtOrder.setUpdateTime(new Date());
        MtOrder.setOperator(orderDto.getOperator());
        MtOrder.setStatus(orderDto.getStatus());

        return orderRepository.save(MtOrder);
    }

    @Override
    public List<MtOrder> getOrderListByParams(Map<String, Object> params) throws BusinessCheckException {
        Specification<MtOrder> specification = orderRepository.buildSpecification(params);
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        List<MtOrder> result = orderRepository.findAll(specification, sort);
        return result;
    }
}
