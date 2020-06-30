package com.fuint.coupon.job;

import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务清空促销缓存数据
 * Created by zach on 2019/9/6.
 */
@Configuration
@EnableScheduling // 启用定时任务
public class CacheClearJob {
    private static final Logger logger = LoggerFactory.getLogger(CacheClearJob.class);

    @Autowired
    private Environment environment;

    @Scheduled(cron = "${cache.clear.time}")
    public void clearCache() {
        String cacheSwitch = environment.getProperty("cache.clear.switch");
        if (StringUtil.equals(cacheSwitch, "1")) {
          // promotionRuleMessageService.promotionRuleResetNotify();
        }
    }
}
