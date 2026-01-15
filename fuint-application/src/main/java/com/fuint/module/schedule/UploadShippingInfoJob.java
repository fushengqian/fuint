package com.fuint.module.schedule;

import com.fuint.common.enums.PayStatusEnum;
import com.fuint.common.service.WeixinService;
import com.fuint.common.util.RedisLock;
import com.fuint.common.util.SeqUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.bean.UploadShippingLogBean;
import com.fuint.repository.mapper.MtUploadShippingLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final Logger logger = LoggerFactory.getLogger(UploadShippingInfoJob.class);

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private MtUploadShippingLogMapper uploadShippingLogMapper;

    /**
     * 微信服务接口
     * */
    @Autowired(required = false)
    private WeixinService weixinService;

    /**
     * 分布式锁
     * */
    @Autowired
    private RedisLock redisLock;

    @Scheduled(cron = "${uploadShippingInfoJob.job.time:0 0/5 * * * ?}")
    @Transactional(rollbackFor = Exception.class)
    public void dealOrder() {
        String lockKey = "lock:uploadShippingInfoJob:deal";
        // 唯一标识当前请求/线程
        String requestId = SeqUtil.getUUID();
        try {
            // 尝试加锁，60秒自动过期
            if (redisLock.tryLock(lockKey, requestId, 60)) {
                String theSwitch = environment.getProperty("uploadShippingInfoJob.job.switch");
                if (theSwitch != null && theSwitch.equals("1")) {
                    logger.info("uploadShippingInfoJobStart!!!");
                    Map<String, Object> param = new HashMap<>();
                    param.put("pay_status", PayStatusEnum.SUCCESS.getValue());
                    List<UploadShippingLogBean> dataList = uploadShippingLogMapper.getUploadShippingLogList(0);
                    if (dataList.size() > 0) {
                        for (UploadShippingLogBean bean : dataList) {
                            try {
                                weixinService.uploadShippingInfo(bean.getOrderSn());
                            } catch (BusinessCheckException e) {
                                logger.error(e.getMessage());
                            }
                        }
                    }
                    logger.info("uploadShippingInfoJobEnd!!!");
                }
            }
        } finally {
            // 释放锁
            redisLock.unlock(lockKey, requestId);
        }
    }
}
