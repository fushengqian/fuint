package com.fuint.module.schedule;

import com.fuint.common.dto.OrderDto;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.enums.PayStatusEnum;
import com.fuint.common.service.OrderService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单自动确认定时任务
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@EnableScheduling
@Component("OrderAutoJob")
public class OrderAutoJob {

    private Logger logger = LoggerFactory.getLogger(OrderAutoJob.class);

    /**
     * 订单服务接口
     */
    @Autowired
    private OrderService orderService;

    @Autowired
    private Environment environment;

    /**
     * 默认订单确认收货1天后，置为已完成
     * */
    private int DELIVERED_OVER_TIME = 60 * 24;

    /**
     * 默认订单发货10天后，置为已收货
     * */
    private int RECEIVED_OVER_TIME = 60 * 24 * 10;

    @Scheduled(cron = "${OrderAutoJob.job.time:0 0/5 * * * ?}")
    @Transactional(rollbackFor = Exception.class)
    public void dealOrder() throws BusinessCheckException {
        String theSwitch = environment.getProperty("orderAutoJob.job.switch");
         if (theSwitch == null || theSwitch.equals("1")) {
             logger.info("OrderAutoJobStart!!!");

             // 已发货，默认10天后确认为已收货
             Map<String, Object> param1 = new HashMap<>();
             param1.put("status", OrderStatusEnum.DELIVERED.getKey());
             param1.put("pay_status", PayStatusEnum.SUCCESS.getKey());
             List<MtOrder> dataList1 = orderService.getOrderListByParams(param1);
             if (dataList1.size() > 0) {
                 for (MtOrder mtOrder : dataList1) {
                      Date overTime = new Date(mtOrder.getCreateTime().getTime() + (60000 * RECEIVED_OVER_TIME));
                      Date nowTime = new Date();
                      if ((overTime.getTime() <= nowTime.getTime())) {
                          OrderDto orderDto = new OrderDto();
                          orderDto.setId(mtOrder.getId());
                          if (mtOrder.getStatus().equals(OrderStatusEnum.DELIVERED.getKey())) {
                              orderDto.setStatus(OrderStatusEnum.RECEIVED.getKey());
                          }
                          orderService.updateOrder(orderDto);
                      }
                 }
             }

             // 已收货，默认1天确认为已完成
             Map<String, Object> param = new HashMap<>();
             param.put("status", OrderStatusEnum.DELIVERED.getKey());
             param.put("pay_status", PayStatusEnum.SUCCESS.getKey());
             List<MtOrder> dataList = orderService.getOrderListByParams(param);
             if (dataList.size() > 0) {
                 for (MtOrder mtOrder : dataList) {
                      Date overTime = new Date(mtOrder.getCreateTime().getTime() + (60000 * DELIVERED_OVER_TIME));
                      Date nowTime = new Date();
                      if ((overTime.getTime() <= nowTime.getTime())) {
                          OrderDto orderDto = new OrderDto();
                          orderDto.setId(mtOrder.getId());
                          if (mtOrder.getStatus().equals(OrderStatusEnum.RECEIVED.getKey())) {
                              orderDto.setStatus(OrderStatusEnum.COMPLETE.getKey());
                          }
                          orderService.updateOrder(orderDto);
                      }
                 }
             }

             logger.info("OrderAutoJobStart!!!");
        }
    }
}
