package com.fuint.module.schedule;

import com.fuint.common.service.MessageService;
import com.fuint.common.service.WeixinService;
import com.fuint.common.util.RedisLock;
import com.fuint.common.util.SeqUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtMessage;
import com.fuint.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 会员消息定时任务
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@EnableScheduling
@Component("messageJob")
public class MessageJob {

    private final Logger logger = LoggerFactory.getLogger(MessageJob.class);

    /**
     * 消息服务接口
     */
    @Autowired(required = false)
    private MessageService messageService;

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

    @Autowired
    private Environment environment;

    /**
     * 一次最多发送消息数量
     **/
    private final int MAX_SEND_NUM = 50;

    @Scheduled(cron = "${message.job.time:0 0/1 * * * ?}")
    @Transactional(rollbackFor = Exception.class)
    public void dealMessage() throws BusinessCheckException {
        String lockKey = "lock:messageJob:deal";
        // 唯一标识当前请求/线程
        String requestId = SeqUtil.getUUID();
        try {
            // 尝试加锁，60秒自动过期
            if (redisLock.tryLock(lockKey, requestId, 60)) {
                String theSwitch = environment.getProperty("message.job.switch");
                if (theSwitch != null && theSwitch.equals("1")) {
                    logger.info("MessageJobStart!!!");
                    List<MtMessage> dataList = messageService.getNeedSendList();
                    if (dataList.size() > 0) {
                        int dealNum = 0;
                        for (MtMessage mtMessage : dataList) {
                            Date nowTime = new Date();
                            // 如果到了发送时间，发送并删除该条消息
                            if (dealNum <= MAX_SEND_NUM && mtMessage.getSendTime().before(nowTime) && StringUtil.isNotEmpty(mtMessage.getParams())) {
                                boolean result = weixinService.doSendSubscribeMessage(mtMessage.getMerchantId(), mtMessage.getParams());
                                messageService.sendMessage(mtMessage.getId(), result);
                                dealNum++;
                            }
                        }
                    }
                    logger.info("MessageJobEnd!!!");
                }
            }
        } finally {
            // 释放锁
            redisLock.unlock(lockKey, requestId);
        }
    }
}
