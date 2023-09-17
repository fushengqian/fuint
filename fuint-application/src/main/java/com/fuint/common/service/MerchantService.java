package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtMerchant;
import java.util.List;
import java.util.Map;

/**
 * 商户业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface MerchantService extends IService<MtMerchant> {

    /**
     * 分页查询商户列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtMerchant> queryMerchantListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 保存商户信息
     *
     * @param  mtMerchant
     * @throws BusinessCheckException
     */
    MtMerchant saveMerchant(MtMerchant mtMerchant) throws BusinessCheckException;

    /**
     * 根据ID获取商户信息
     *
     * @param  id 商户ID
     * @throws BusinessCheckException
     */
    MtMerchant queryMerchantById(Integer id) throws BusinessCheckException;

    /**
     * 根据名称获取商户信息
     *
     * @param  name 商户名称
     * @throws BusinessCheckException
     */
    MtMerchant queryMerchantByName(String name) throws BusinessCheckException;

    /**
     * 根据商户号获取商户信息
     *
     * @param  merchantNo 商户号
     * @return
     */
    MtMerchant queryMerchantByNo(String merchantNo);

    /**
     * 根据商户号获取商户ID
     *
     * @param  merchantNo 商户号
     * @return
     */
    Integer getMerchantId(String merchantNo);

    /**
     * 更新商户状态
     *
     * @param id       商户ID
     * @param operator 操作人
     * @param status   状态
     * @throws BusinessCheckException
     */
    void updateStatus(Integer id, String operator, String status) throws BusinessCheckException;

    /**
     * 根据条件查询商户
     * */
    List<MtMerchant> queryMerchantByParams(Map<String, Object> params) throws BusinessCheckException;

}
