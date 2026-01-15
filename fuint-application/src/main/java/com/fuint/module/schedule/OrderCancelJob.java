package com.fuint.module.schedule;

import com.fuint.common.dto.OrderDto;
import com.fuint.common.enums.OrderStatusEnum;
import com.fuint.common.enums.PayStatusEnum;
import com.fuint.common.service.OrderService;
import com.fuint.common.util.RedisLock;
import com.fuint.common.util.SeqUtil;
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
 * 订单超时处理定时任务
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@EnableScheduling
@Component("orderCancelJob")
public class OrderCancelJob {

    private final Logger logger = LoggerFactory.getLogger(OrderCancelJob.class);

    /**
     * 订单服务接口
     */
    @Autowired
    private OrderService orderService;

    /**
     * 分布式锁
     * */
    @Autowired
    private RedisLock redisLock;

    @Autowired
    private Environment environment;

    /**
     * 一次最多处理订单数量
     **/
    private final int MAX_SEND_NUM = 50;

    /**
     * 订单超时分钟
     * */
    private final int OVER_TIME = 30;

    @Scheduled(cron = "${orderCancel.job.time:0 0/2 * * * ?}")
    @Transactional(rollbackFor = Exception.class)
    public void dealOrder() throws BusinessCheckException {
        String lockKey = "lock:orderCancelJob:deal";
        // 唯一标识当前请求/线程
        String requestId = SeqUtil.getUUID();
        try {
            // 尝试加锁，60秒自动过期
            if (redisLock.tryLock(lockKey, requestId, 60)) {
                String theSwitch = environment.getProperty("orderCancel.job.switch");
                 if (theSwitch != null && theSwitch.equals("1")) {
                    logger.info("OrderCancelJobStart!!!");
                    Map<String, Object> param = new HashMap<>();
                    param.put("status", OrderStatusEnum.CREATED.getKey());
                    List<MtOrder> dataList = orderService.getOrderListByParams(param);
                    if (dataList.size() > 0) {
                        int dealNum = 0;
                        for (MtOrder mtOrder : dataList) {
                             Date overTime = new Date(mtOrder.getCreateTime().getTime() + (60000 * OVER_TIME));
                             Date nowTime = new Date();
                             // 超时关闭订单
                             if (dealNum <= MAX_SEND_NUM && (overTime.getTime() <= nowTime.getTime())) {
                                 if (mtOrder.getPayStatus().equals(PayStatusEnum.WAIT.getKey())) {
                                     orderService.cancelOrder(mtOrder.getId(), "超时未支付取消");
                                     dealNum++;
                                 } else if (mtOrder.getPayStatus().equals(PayStatusEnum.SUCCESS.getKey())) {
                                     OrderDto reqDto = new OrderDto();
                                     reqDto.setId(mtOrder.getId());
                                     reqDto.setStatus(OrderStatusEnum.PAID.getKey());
                                     orderService.updateOrder(reqDto);
                                     orderService.setOrderPayed(mtOrder.getId(), null);
                                 }
                             }
                        }
                    }
                    logger.info("OrderCancelJobEnd!!!");
                }
            }
        } finally {
            // 释放锁
            redisLock.unlock(lockKey, requestId);
        }
    }
}
