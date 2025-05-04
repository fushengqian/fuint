package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.StoreDto;
import com.fuint.common.dto.StoreInfo;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtStore;
import java.util.List;
import java.util.Map;

/**
 * 店铺业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface StoreService extends IService<MtStore> {

    /**
     * 分页查询店铺列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<StoreDto> queryStoreListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 保存店铺信息
     *
     * @param  reqStoreDto
     * @throws BusinessCheckException
     */
    MtStore saveStore(StoreDto reqStoreDto) throws BusinessCheckException;

    /**
     * 获取系统默认店铺
     *
     * @throws BusinessCheckException
     * @return
     */
    MtStore getDefaultStore(String merchantNo) throws BusinessCheckException;

    /**
     * 根据店铺ID获取店铺信息
     *
     * @param  id 店铺ID
     * @throws BusinessCheckException
     * @return
     */
    MtStore queryStoreById(Integer id) throws BusinessCheckException;

    /**
     * 根据店铺名称获取店铺信息
     *
     * @param  storeName 店铺名称
     * @throws BusinessCheckException
     */
    StoreDto queryStoreByName(String storeName) throws BusinessCheckException;

    /**
     * 根据店铺ID查询店铺信息
     *
     * @param  id 店铺ID
     * @return
     * @throws BusinessCheckException
     */
    StoreDto queryStoreDtoById(Integer id) throws BusinessCheckException;

    /**
     * 更新店铺状态
     *
     * @param  id       店铺ID
     * @param  operator 操作人
     * @param  status   状态
     * @throws BusinessCheckException
     */
    void updateStatus(Integer id, String operator, String status) throws BusinessCheckException;

    /**
     * 根据条件查询店铺列表
     *
     * @param params 查询参数
     * @return
     * */
    List<MtStore> queryStoresByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 根据距离远近查找店铺
     *
     * @param merchantNo 商户号
     * @param keyword 关键字
     * @param latitude 维度
     * @param longitude 经度
     * @return
     * */
    List<StoreInfo> queryByDistance(String merchantNo, String keyword, String latitude, String longitude) throws BusinessCheckException;

    /**
     * 获取店铺名称
     *
     * @param storeIds 店铺ID
     * @return
     * */
    String getStoreNames(String storeIds);

    /**
     * 获取店铺ID
     *
     * @param merchantId 商户ID
     * @param storeNames 店铺名称
     * @return
     * */
    String getStoreIds(Integer merchantId, String storeNames);

    /**
     * 根据商户ID删除店铺信息
     *
     * @param merchantId 商户ID
     * @return
     * */
    void deleteStoreByMerchant(Integer merchantId);

}
