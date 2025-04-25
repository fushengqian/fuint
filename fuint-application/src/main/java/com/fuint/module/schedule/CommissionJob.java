package com.fuint.module.schedule;

import com.fuint.common.enums.PayStatusEnum;
import com.fuint.common.service.CommissionLogService;
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
import java.util.*;

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

    /**
     * 分佣记录服务接口
     * */
    @Autowired
    private CommissionLogService commissionLogService;

    /**
     * 系统环境变量
     * */
    @Autowired
    private Environment environment;

    /**
     * 一次最多处理订单数量
     **/
    private int MAX_ROWS = 10;

    /**
     * 订单完成后n天可产生佣金
     * */
    private int OVER_DAY = 0;

    @Scheduled(cron = "${commission.job.time}")
    @Transactional(rollbackFor = Exception.class)
    public void dealOrder() throws BusinessCheckException {
        String theSwitch = environment.getProperty("commission.job.switch");
         if (theSwitch != null && theSwitch.equals("1")) {
             logger.info("CommissionJobStart!!!");
             Map<String, Object> param = new HashMap<>();
             param.put("PAY_STATUS", PayStatusEnum.SUCCESS.getKey());

             Calendar calendar = Calendar.getInstance();
             calendar.add(Calendar.DATE, -OVER_DAY);
             Date dateTime = calendar.getTime();
             String endTime = DateUtil.formatDate(dateTime, "yyyy-MM-dd HH:mm:ss");

             List<MtOrder> dataList = orderService.getTobeCommissionOrderList(endTime);
             if (dataList.size() > 0) {
                int dealNum = 0;
                for (MtOrder mtOrder : dataList) {
                     // 计算订单佣金
                     if (dealNum <= MAX_ROWS) {
                         commissionLogService.calculateCommission(mtOrder.getId());
                         dealNum++;
                     }
                }
             }
             logger.info("CommissionJobEnd!!!");
        }
    }
}
