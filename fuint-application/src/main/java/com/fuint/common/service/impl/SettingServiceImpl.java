package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.ParamDto;
import com.fuint.common.enums.*;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.repository.mapper.MtSettingMapper;
import com.fuint.repository.model.MtSetting;
import com.fuint.common.service.SettingService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@AllArgsConstructor(onConstructor_= {@Lazy})
public class SettingServiceImpl extends ServiceImpl<MtSettingMapper, MtSetting> implements SettingService {

    /**
     * 系统环境变量
     * */
    private Environment env;

    private MtSettingMapper mtSettingMapper;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 删除配置
     *
     * @param  merchantId 商户ID
     * @param  type 类型
     * @param  name 配置名称
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @OperationServiceLog(description = "删除配置信息")
    public void removeSetting(Integer merchantId, String type, String name) {
        MtSetting info = querySettingByName(merchantId, type, name);
        if (info != null) {
            mtSettingMapper.deleteById(info.getId());
        }
    }

    /**
     * 保存配置
     *
     * @param  mtSetting 配置参数
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "保存配置信息")
    public MtSetting saveSetting(MtSetting mtSetting) {
        MtSetting settingInfo = querySettingByName(mtSetting.getMerchantId(), mtSetting.getType(), mtSetting.getName());
        if (null != settingInfo) {
            if (mtSetting.getValue() != null) {
                settingInfo.setValue(mtSetting.getValue());
            }
            if (mtSetting.getDescription() != null) {
                settingInfo.setDescription(mtSetting.getDescription());
            }
            if (StringUtil.isNotEmpty(mtSetting.getOperator())) {
                settingInfo.setOperator(mtSetting.getOperator());
            }
            if (mtSetting.getUpdateTime() != null) {
                settingInfo.setUpdateTime(mtSetting.getUpdateTime());
            }
            if (mtSetting.getStatus() != null) {
                settingInfo.setStatus(mtSetting.getStatus());
            }
            if (mtSetting.getType() != null) {
                settingInfo.setType(mtSetting.getType());
            }
            mtSettingMapper.updateById(settingInfo);
        } else {
            // 创建配置
            if (mtSetting.getName() != null && mtSetting.getValue() != null) {
                mtSetting.setCreateTime(new Date());
                mtSetting.setStatus(StatusEnum.ENABLED.getKey());
                mtSettingMapper.insert(mtSetting);
            }
        }

        return mtSetting;
    }

    /**
     * 获取配置列表
     *
     * @param  merchantId 商户ID
     * @param  type 配置类型
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public List<MtSetting> getSettingList(Integer merchantId, String type) {
        return mtSettingMapper.querySettingByType(merchantId, type);
    }

    /**
     * 根据ID获取配置信息
     *
     * @param  merchantId 商户ID
     * @param  type 类型
     * @param  name 配置名称
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtSetting querySettingByName(Integer merchantId, String type, String name) {
        return mtSettingMapper.querySettingByName(merchantId, 0, type, name);
    }

    /**
     * 根据ID获取配置信息
     *
     * @param  merchantId 商户ID
     * @param  storeId 店铺ID
     * @param  type 类型
     * @param  name 配置名称
     * @throws BusinessCheckException
     * @return
     */
    @Override
    public MtSetting querySettingByName(Integer merchantId, Integer storeId, String type, String name) {
        return mtSettingMapper.querySettingByName(merchantId, storeId, type, name);
    }

    /**
     * 获取系统上传的根路径
     *
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
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param platform 平台
     * @return
     * */
    @Override
    public List<ParamDto> getPayTypeList(Integer merchantId, Integer storeId, String platform) throws BusinessCheckException {
        List<ParamDto> payTypeList = new ArrayList<>();

        // 微信jsapi
        ParamDto jsApi = new ParamDto(PayTypeEnum.JSAPI.getKey(), PayTypeEnum.JSAPI.getValue(), PayTypeEnum.JSAPI.getKey());
        payTypeList.add(jsApi);

        // 余额支付
        ParamDto balance = new ParamDto(PayTypeEnum.BALANCE.getKey(), PayTypeEnum.BALANCE.getValue(), PayTypeEnum.BALANCE.getKey());
        payTypeList.add(balance);

        // 前台支付
        MtSetting mtSetting = settingService.querySettingByName(merchantId, storeId,  SettingTypeEnum.ORDER.getKey(), OrderSettingEnum.PAY_OFF_LINE.getKey());
        if (mtSetting != null && mtSetting.getValue().equals(YesOrNoEnum.YES.getKey())) {
            ParamDto store = new ParamDto(PayTypeEnum.STORE.getKey(), PayTypeEnum.STORE.getValue(), PayTypeEnum.STORE.getKey());
            payTypeList.add(store);
        }

        return payTypeList;
    }
}
