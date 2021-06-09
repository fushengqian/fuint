package com.fuint.application.service.setting;

import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.dao.repositories.MtSettingRepository;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.exception.BusinessCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 配置业务接口实现类
 * Created by zach 2021/5/16
 */
@Service
public class SettingServiceImpl implements SettingService {

    private static final Logger log = LoggerFactory.getLogger(SettingServiceImpl.class);

    @Autowired
    private MtSettingRepository settingRepository;

    /**
     * 保存配置
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "保存配置信息")
    public MtSetting saveSetting(MtSetting reqDto) throws BusinessCheckException {
        MtSetting info = this.querySettingByName(reqDto.getName());

        if (null != info) {
            // 更新
            MtSetting newInfo = new MtSetting();
            BeanUtils.copyProperties(reqDto, newInfo);
            newInfo.setId(info.getId());
            return settingRepository.save(newInfo);
        } else {
            // 创建
            reqDto.setCreateTime(new Date());
            return settingRepository.save(reqDto);
        }
    }

    /**
     * 获取配置列表
     *
     * @param type
     * @throws BusinessCheckException
     */
    @Override
    public List<MtSetting> getSettingList(String type) throws BusinessCheckException {
        List<MtSetting> dataList = settingRepository.querySettingByType(type);
        return dataList;
    }

    /**
     * 根据ID获取配置信息
     *
     * @param name name
     * @throws BusinessCheckException
     */
    @Override
    public MtSetting querySettingByName(String name) throws BusinessCheckException {
        MtSetting setting = settingRepository.querySettingByName(name);
        return setting;
    }
}
