package com.fuint.module.schedule;

import com.fuint.common.service.MessageService;
import com.fuint.common.service.WeixinService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.repository.model.MtMessage;
import com.fuint.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(MessageJob.class);

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

    @Autowired
    private Environment environment;

    /**
     * 一次最多发送消息数量
     **/
    private int MAX_SEND_NUM = 50;

    @Scheduled(cron = "${message.job.time}")
    @Transactional(rollbackFor = Exception.class)
    public void dealMessage() throws BusinessCheckException {
        String theSwitch = environment.getProperty("message.job.switch");
        if (theSwitch.equals("1")) {
            logger.info("MessageJobStart!!!");
            List<MtMessage> dataList = messageService.getNeedSendList();
            if (dataList.size() > 0) {
                int dealNum = 0;
                for (MtMessage mtMessage : dataList) {
                    Date nowTime = new Date();
                    // 如果到了发送时间，发送并删除该条消息
                    if (dealNum <= MAX_SEND_NUM && mtMessage.getSendTime().before(nowTime) && StringUtil.isNotEmpty(mtMessage.getParams())) {
                        boolean result = weixinService.doSendSubscribeMessage(mtMessage.getParams());
                        messageService.sendMessage(mtMessage.getId(), result);
                        dealNum++;
                    }
                }
            }
            logger.info("MessageJobEnd!!!");
        }
    }
}
