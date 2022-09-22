package com.fuint.application.service.address;

import com.fuint.application.dao.entities.MtAddress;
import com.fuint.application.dao.repositories.MtAddressRepository;
import com.fuint.base.annoation.OperationServiceLog;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.enums.StatusEnum;
import com.fuint.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * 收货地址业务实现类
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
 */
@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Autowired
    private MtAddressRepository addressRepository;

    /**
     * 保存收货地址
     *
     * @param mtAddress
     * @throws BusinessCheckException
     */
    @Override
    @OperationServiceLog(description = "保存收货地址")
    @Transactional
    public MtAddress saveAddress(MtAddress mtAddress) throws BusinessCheckException {
        if (mtAddress.getId() > 0) {
            MtAddress address = addressRepository.findOne(mtAddress.getId());
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
                if (mtAddress.getIsDefault().equals("Y")) {
                    addressRepository.setDefault(mtAddress.getUserId(), mtAddress.getId());
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

            return addressRepository.save(address);
        } else {
            mtAddress.setCreateTime(new Date());
            mtAddress.setUpdateTime(new Date());
            mtAddress.setIsDefault("Y");

            MtAddress result = addressRepository.save(mtAddress);
            addressRepository.setDefault(mtAddress.getUserId(), result.getId());
            return result;
        }
    }

    /**
     * 根据ID获取收货地址
     *
     * @param id
     * @throws BusinessCheckException
     */
    @Override
    public MtAddress detail(Integer id) throws BusinessCheckException {
        return addressRepository.findOne(id);
    }

    @Override
    public List<MtAddress> queryListByParams(Map<String, Object> params) {
        Map<String, Object> param = new HashMap<>();

        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        param.put("EQ_status", status);

        if (params.get("userId") != null) {
            param.put("EQ_userId", params.get("userId").toString());
        }
        if (params.get("isDefault") != null) {
            param.put("EQ_isDefault", "Y");
        }

        Specification<MtAddress> specification = addressRepository.buildSpecification(param);
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        List<MtAddress> result = addressRepository.findAll(specification, sort);

        return result;
    }
}
