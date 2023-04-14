package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.PayTypeEnum;
import com.fuint.common.enums.PlatformTypeEnum;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.repository.mapper.MtSettingMapper;
import com.fuint.repository.model.MtSetting;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.service.SettingService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 配置业务接口实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class SettingServiceImpl extends ServiceImpl<MtSettingMapper, MtSetting> implements SettingService {

    @Autowired
    private Environment env;

    @Resource
    private MtSettingMapper mtSettingMapper;

    /**
     * 删除配置
     *
     * @param  name
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "删除配置信息")
    public void removeSetting(String name) {
        MtSetting info = this.querySettingByName(name);
        if (info != null) {
            mtSettingMapper.deleteById(info.getId());
        }
        return;
    }

    /**
     * 保存配置
     *
     * @param mtSetting
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存配置信息")
    public MtSetting saveSetting(MtSetting mtSetting) {
        MtSetting info = this.querySettingByName(mtSetting.getName());
        if (null != info) {
            if (mtSetting.getValue() != null) {
                info.setValue(mtSetting.getValue());
            }
            if (mtSetting.getDescription() != null) {
                info.setDescription(mtSetting.getDescription());
            }
            if (StringUtil.isNotEmpty(mtSetting.getOperator())) {
                info.setOperator(mtSetting.getOperator());
            }
            if (mtSetting.getUpdateTime() != null) {
                info.setUpdateTime(mtSetting.getUpdateTime());
            }
            if (mtSetting.getStatus() != null) {
                info.setStatus(mtSetting.getStatus());
            }
            if (mtSetting.getType() != null) {
                info.setType(mtSetting.getType());
            }
            mtSettingMapper.updateById(info);
        } else {
            // 创建配置
            mtSetting.setCreateTime(new Date());
            mtSetting.setStatus(StatusEnum.ENABLED.getKey());
            mtSettingMapper.insert(mtSetting);
        }

        return mtSetting;
    }

    /**
     * 获取配置列表
     *
     * @param type
     * @throws BusinessCheckException
     */
    @Override
    public List<MtSetting> getSettingList(String type) {
        List<MtSetting> dataList = mtSettingMapper.querySettingByType(type);
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
        return mtSettingMapper.querySettingByName(name);
    }

    /**
     * 获取系统上传的根路径
     * @return
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
                if (StringUtil.isNotEmpty(domain)) {
                    basePath = domain;
                }
            }
        }

        return basePath;
    }

    /**
     * 获取支付方式列表
     * @param platform
     * @return
     * */
    @Override
    public List<ParamDto> getPayTypeList(String platform) {
        List<ParamDto> payTypeList = new ArrayList<>();

        // 微信jsapi
        ParamDto jsApi = new ParamDto();
        jsApi.setKey(PayTypeEnum.JSAPI.getKey());
        jsApi.setValue(PayTypeEnum.JSAPI.getKey());
        jsApi.setName(PayTypeEnum.JSAPI.getValue());

        // 余额支付
        ParamDto balance = new ParamDto();
        balance.setKey(PayTypeEnum.BALANCE.getKey());
        balance.setValue(PayTypeEnum.BALANCE.getKey());
        balance.setName(PayTypeEnum.BALANCE.getValue());
        payTypeList.add(balance);

        // 扫码支付
        ParamDto micro = new ParamDto();
        micro.setKey(PayTypeEnum.MICROPAY.getKey());
        micro.setValue(PayTypeEnum.MICROPAY.getKey());
        micro.setValue(PayTypeEnum.MICROPAY.getValue());

        // 微信公众号号
        if (platform.equals(PlatformTypeEnum.MP_WEIXIN.getCode())) {
            payTypeList.add(jsApi);
        }

        return payTypeList;
    }
}
