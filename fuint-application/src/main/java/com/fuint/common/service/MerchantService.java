package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.merchant.MerchantDto;
import com.fuint.common.dto.merchant.MerchantSettingDto;
import com.fuint.common.dto.system.AccountInfo;
import com.fuint.common.param.MerchantPage;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.module.merchantApi.request.MerchantSettingParam;
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
     * @param merchantPage
     * @return
     */
    PaginationResponse<MerchantDto> queryMerchantListByPagination(MerchantPage merchantPage);

    /**
     * 保存商户信息
     *
     * @param  mtMerchant
     * @throws BusinessCheckException
     * @return
     */
    MtMerchant saveMerchant(MtMerchant mtMerchant) throws BusinessCheckException;

    /**
     * 根据ID获取商户信息
     *
     * @param  id 商户ID
     * @return
     */
    MtMerchant queryMerchantById(Integer id);

    /**
     * 根据名称获取商户信息
     *
     * @param  name 商户名称
     * @return
     */
    MtMerchant queryMerchantByName(String name);

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
     * @return
     */
    void updateStatus(Integer id, String operator, String status) throws BusinessCheckException;

    /**
     * 根据条件查询商户
     *
     * @param params 查询参数
     * @return
     * */
    List<MtMerchant> queryMerchantByParams(Map<String, Object> params);

    /**
     * 查询我的商户列表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param status 状态
     * @return
     * */
    List<MtMerchant> getMyMerchantList(Integer merchantId, Integer storeId, String status);

    /**
     * 获取商户信息
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @return
     * */
    MerchantSettingDto getMerchantSettingInfo(Integer merchantId, Integer storeId);

    /**
     * 保存商户设置信息
     *
     * @param params 商户设置项
     * @param accountInfo 登录账号信息
     * @return
     * */
    MerchantSettingDto saveMerchantSetting(MerchantSettingParam params, AccountInfo accountInfo) throws BusinessCheckException;

}
