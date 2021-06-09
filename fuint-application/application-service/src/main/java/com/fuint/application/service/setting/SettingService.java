package com.fuint.application.service.setting;

import com.fuint.application.dao.entities.MtSetting;
import com.fuint.exception.BusinessCheckException;
import java.util.List;

/**
 * 配置业务接口
 * Created by zach 2021/5/16
 */
public interface SettingService {
    /**
     * 保存配置
     *
     * @param reqDto
     * @throws BusinessCheckException
     */
    MtSetting saveSetting(MtSetting reqDto) throws BusinessCheckException;

    /**
     * 获取配置列表
     *
     * @param type
     * @throws BusinessCheckException
     */
    List<MtSetting> getSettingList(String type) throws BusinessCheckException;

    /**
     * 根据ID获取配置信息
     *
     * @param name name
     * @throws BusinessCheckException
     */
    MtSetting querySettingByName(String name) throws BusinessCheckException;
}
