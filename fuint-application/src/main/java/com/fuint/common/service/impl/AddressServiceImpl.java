package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.service.AddressService;
import com.fuint.repository.model.MtAddress;
import com.fuint.repository.mapper.MtAddressMapper;
import com.fuint.common.enums.YesOrNoEnum;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.common.enums.StatusEnum;
import com.fuint.utils.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * 收货地址业务实现类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class AddressServiceImpl extends ServiceImpl<MtAddressMapper, MtAddress> implements AddressService {

    @Resource
    private MtAddressMapper mtAddressMapper;

    /**
     * 保存收货地址
     *
     * @param mtAddress
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MtAddress saveAddress(MtAddress mtAddress) {
        if (mtAddress.getId() > 0) {
            MtAddress address = mtAddressMapper.selectById(mtAddress.getId());
            if (StringUtil.isNotEmpty(mtAddress.getName())) {
                address.setName(mtAddress.getName());
            }
            if (StringUtil.isNotEmpty(mtAddress.getMobile())) {
                address.setMobile(mtAddress.getMobile());
            }
            if (StringUtil.isNotEmpty(mtAddress.getDetail())) {
                address.setDetail(mtAddress.getDetail());
            }
            if (StringUtil.isNotEmpty(mtAddress.getIsDefault())) {
                if (mtAddress.getIsDefault().equals(YesOrNoEnum.YES.getKey())) {
                    mtAddressMapper.setDefault(mtAddress.getUserId(), mtAddress.getId());
                }
                address.setIsDefault(mtAddress.getIsDefault());
            }
            if (StringUtil.isNotEmpty(mtAddress.getStatus())) {
                address.setStatus(mtAddress.getStatus());
            }
            if (mtAddress.getProvinceId() > 0) {
                address.setProvinceId(mtAddress.getProvinceId());
            }
            if (mtAddress.getCityId() > 0) {
                address.setCityId(mtAddress.getCityId());
            }
            if (mtAddress.getRegionId() > 0) {
                address.setRegionId(mtAddress.getRegionId());
            }

            mtAddressMapper.updateById(address);
        } else {
            mtAddress.setCreateTime(new Date());
            mtAddress.setUpdateTime(new Date());
            mtAddress.setIsDefault(YesOrNoEnum.YES.getKey());

            this.save(mtAddress);
            mtAddressMapper.setDefault(mtAddress.getUserId(), mtAddress.getId());
        }

        return mtAddress;
    }

    /**
     * 根据ID获取收货地址
     *
     * @param id
     * @throws BusinessCheckException
     */
    @Override
    public MtAddress detail(Integer id) {
        return mtAddressMapper.selectById(id);
    }

    @Override
    public List<MtAddress> queryListByParams(Map<String, Object> params) {
        Map<String, Object> param = new HashMap<>();

        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        param.put("status", status);

        if (params.get("userId") != null) {
            param.put("user_id", params.get("userId").toString());
        }

        if (params.get("isDefault") != null) {
            param.put("is_default", YesOrNoEnum.YES.getKey());
        }

        List<MtAddress> result = mtAddressMapper.selectByMap(param);

        return result;
    }
}
