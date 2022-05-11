package com.fuint.application.service.setting;

import com.fuint.application.dao.entities.MtSetting;
import com.fuint.application.dao.repositories.MtSettingRepository;
import com.fuint.application.enums.StatusEnum;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.exception.BusinessCheckException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * 配置业务接口实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private Environment env;

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
            if (reqDto.getValue() != null) {
                info.setValue(reqDto.getValue());
            }
            if (reqDto.getDescription() != null) {
                info.setDescription(reqDto.getDescription());
            }
            if (StringUtils.isNotEmpty(reqDto.getOperator())) {
                info.setOperator(reqDto.getOperator());
            }
            if (reqDto.getUpdateTime() != null) {
                info.setUpdateTime(reqDto.getUpdateTime());
            }
            if (reqDto.getStatus() != null) {
                info.setStatus(reqDto.getStatus());
            }
            if (reqDto.getType() != null) {
                info.setType(reqDto.getType());
            }
            return settingRepository.save(info);
        } else {
            // 创建配置
            reqDto.setCreateTime(new Date());
            reqDto.setStatus(StatusEnum.ENABLED.getKey());
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
     * @param name
     * @throws BusinessCheckException
     */
    @Override
    public MtSetting querySettingByName(String name) {
        MtSetting setting = settingRepository.querySettingByName(name);
        return setting;
    }

    /**
     * 获取系统上传的根路径
     * */
    @Override
    public String getUploadBasePath() {
        String basePath = env.getProperty("images.upload.url");

        String mode = env.getProperty("aliyun.oss.mode");
        if (mode == null) {
            return basePath;
        } else {
            if (mode.equals("1")) {
                String domain = env.getProperty("aliyun.oss.domain");
                if (StringUtils.isNotEmpty(domain)) {
                    basePath = domain;
                }
            }
        }

        return basePath;
    }
}
