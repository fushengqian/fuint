package com.fuint.module.schedule;

import com.fuint.common.enums.PayStatusEnum;
import com.fuint.common.service.OrderService;
import com.fuint.common.service.WeixinService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信小程序上传发货处理定时任务
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@EnableScheduling
@Component("UploadShippingInfoJob")
public class UploadShippingInfoJob {

    private Logger logger = LoggerFactory.getLogger(UploadShippingInfoJob.class);

    @Autowired
    private Environment environment;

    /**
     * 订单服务接口
     */
    @Autowired(required = false)
    private OrderService orderService;

    /**
     * 微信服务接口
     * */
    @Autowired(required = false)
    private WeixinService weixinService;

    @Scheduled(cron = "${uploadShippingInfoJob.job.time}")
    @Transactional(rollbackFor = Exception.class)
    public void dealOrder() throws BusinessCheckException {
        String theSwitch = environment.getProperty("uploadShippingInfoJob.job.switch");
         if (theSwitch != null && theSwitch.equals("1")) {
            logger.info("uploadShippingInfoJobStart!!!");
            Map<String, Object> param = new HashMap<>();
            param.put("pay_status", PayStatusEnum.SUCCESS.getValue());
            List<MtOrder> dataList = orderService.getOrderListByParams(param);
            if (dataList.size() > 0) {
                for (MtOrder mtOrder : dataList) {
                     weixinService.uploadShippingInfo(mtOrder.getOrderSn());
                }
            }
            logger.info("uploadShippingInfoJobEnd!!!");
        }
    }
}
