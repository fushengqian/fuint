package com.fuint.common.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fuint.common.Constants;
import com.fuint.repository.mapper.MtSettingMapper;
import com.fuint.repository.model.MtSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.List;

/**
 * 系统初始化
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class SystemInit implements InitializingBean, ServletContextAware {

    @Resource
    private MtSettingMapper mtSettingMapper;

    public static final Logger logger = LoggerFactory.getLogger(SystemInit.class);

    @Override
    public void afterPropertiesSet() {
        // empty
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        logger.info("Executing system initialization");
        logger.info("Initial system config");
        // 加载系统配置
        List<MtSetting> sysConfigList = mtSettingMapper.selectList(new LambdaQueryWrapper<MtSetting>().eq(MtSetting::getType,"system"));
        for (MtSetting setting : sysConfigList) {
             if (setting.getType().equals("system")) {
                 Constants.SYS_CONFIGS.put(setting.getId().toString(), setting.getValue());
             }
        }
        logger.info("Completed system initialization");
    }
}
