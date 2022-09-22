package com.fuint.application.job;

import com.fuint.application.dao.entities.MtOrder;
import com.fuint.application.dto.OrderDto;
import com.fuint.application.enums.OrderStatusEnum;
import com.fuint.application.enums.PayStatusEnum;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.fuint.application.service.order.OrderService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单超时处理定时任务
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Configuration
@EnableScheduling
public class OrderCancelJob {

    private static final Logger logger = LoggerFactory.getLogger(OrderCancelJob.class);

    /**
     * 订单服务接口
     */
    @Autowired(required = false)
    private OrderService orderService;

    @Autowired
    private Environment environment;

    /**
     * 一次最多发送消息数量
     **/
    private int MAX_SEND_NUM = 50;

    /**
     * 订单超时分钟
     * */
    private int OVER_TIME = 30;

    @Scheduled(cron = "${orderCancel.job.time}")
    public void scheduled() throws BusinessCheckException {
        String theSwitch = environment.getProperty("orderCancel.job.switch");
        if (theSwitch.equals("1")) {
            logger.debug("OrderCancelJobStart!!!");
            Map<String, Object> param = new HashMap<>();
            param.put("EQ_status", OrderStatusEnum.CREATED.getKey());
            List<MtOrder> dataList = orderService.getOrderListByParams(param);
            if (dataList.size() > 0) {
                int dealNum = 0;
                for (MtOrder mtOrder : dataList) {
                    Date overTime = new Date(mtOrder.getCreateTime().getTime() + (60000 * OVER_TIME));
                    // 超时关闭订单
                    if (dealNum <= MAX_SEND_NUM && mtOrder.getCreateTime().before(overTime)) {
                        if (mtOrder.getPayStatus().equals(PayStatusEnum.WAIT.getKey())) {
                            orderService.cancelOrder(mtOrder.getId(), "超时未支付取消");
                            dealNum++;
                        } else if (mtOrder.getPayStatus().equals(PayStatusEnum.SUCCESS.getKey())) {
                            OrderDto reqDto = new OrderDto();
                            reqDto.setId(mtOrder.getId());
                            reqDto.setStatus(OrderStatusEnum.PAID.getKey());
                            orderService.updateOrder(reqDto);
                        }
                    }
                }
            }
            logger.debug("OrderCancelJobEnd!!!");
        }
    }
}
