package com.fuint.module.schedule;

import com.fuint.common.enums.PayStatusEnum;
import com.fuint.common.service.OrderService;
import com.fuint.common.util.DateUtil;
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
 * 分销提成计算定时任务
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@EnableScheduling
@Component("CommissionJob")
public class CommissionJob {

    private Logger logger = LoggerFactory.getLogger(CommissionJob.class);

    /**
     * 订单服务接口
     */
    @Autowired
    private OrderService orderService;

    @Autowired
    private Environment environment;

    /**
     * 一次最多处理订单数量
     **/
    private int MAX_SEND_NUM = 50;

    /**
     * 订单超时分钟
     * */
    private int OVER_TIME = 30;

    @Scheduled(cron = "${commission.job.time}")
    @Transactional(rollbackFor = Exception.class)
    public void dealOrder() throws BusinessCheckException {
        String theSwitch = environment.getProperty("commission.job.switch");
         if (theSwitch.equals("1")) {
            logger.info("CommissionJobStart!!!");
            Map<String, Object> param = new HashMap<>();
            param.put("PAY_STATUS", PayStatusEnum.SUCCESS.getKey());
            String dateTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
            List<MtOrder> dataList = orderService.getTobeCommissionOrderList(dateTime);
            if (dataList.size() > 0) {
                int dealNum = 0;
                for (MtOrder mtOrder : dataList) {
                     Date overTime = new Date(mtOrder.getCreateTime().getTime() + (60000 * OVER_TIME));
                     Date nowTime = new Date();
                     // 处理订单
                     if (dealNum <= MAX_SEND_NUM && (overTime.getTime() <= nowTime.getTime())) {
                         dealNum++;
                     }
                }
            }
            logger.info("CommissionJobEnd!!!");
        }
    }
}
