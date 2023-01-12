package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.repository.model.MtAddress;
import com.fuint.framework.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 收货地址业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface AddressService extends IService<MtAddress> {

    /**
     * 保存收货地址
     *
     * @param  mtAddress
     * @throws BusinessCheckException
     */
    MtAddress saveAddress(MtAddress mtAddress) throws BusinessCheckException;

    /**
     * 根据ID获取Banner信息
     *
     * @param id Banner ID
     * @throws BusinessCheckException
     */
    MtAddress detail(Integer id) throws BusinessCheckException;

    /**
     * 根据条件搜索
     * */
    List<MtAddress> queryListByParams(Map<String, Object> params) throws BusinessCheckException;
}
