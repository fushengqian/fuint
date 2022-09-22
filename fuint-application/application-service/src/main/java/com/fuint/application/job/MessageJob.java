package com.fuint.application.job;

import com.fuint.application.dao.entities.MtMessage;
import com.fuint.application.service.weixin.WeixinService;
import com.fuint.exception.BusinessCheckException;
import com.fuint.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.fuint.application.service.message.MessageService;
import java.util.Date;
import java.util.List;

/**
 * 会员消息定时任务
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Configuration
@EnableScheduling
public class MessageJob {

    private static final Logger logger = LoggerFactory.getLogger(MessageJob.class);

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
    public void scheduled() throws BusinessCheckException {
        String theSwitch = environment.getProperty("message.job.switch");
        if (theSwitch.equals("1")) {
            logger.debug("MessageJobStart!!!");
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
            logger.debug("MessageJobEnd!!!");
        }
    }
}
