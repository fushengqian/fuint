package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.StoreDto;
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
    PaginationResponse<MtStore> queryStoreListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 保存店铺信息
     *
     * @param reqStoreDto
     * @throws BusinessCheckException
     */
    MtStore saveStore(StoreDto reqStoreDto) throws BusinessCheckException;

    /**
     * 获取系统默认店铺
     *
     * @throws BusinessCheckException
     */
    MtStore getDefaultStore() throws BusinessCheckException;

    /**
     * 根据店铺ID获取店铺信息
     *
     * @param  id 店铺ID
     * @throws BusinessCheckException
     */
    MtStore queryStoreById(Integer id) throws BusinessCheckException;

    /**
     * 根据店铺id列表获取店铺信息
     *
     * @param ids 店铺ID列表
     * @throws BusinessCheckException
     */
    List<MtStore> queryStoresByIds(List<Integer> ids) throws BusinessCheckException;

    /**
     * 根据店铺名称获取店铺信息
     *
     * @param storeName 店铺名称
     * @throws BusinessCheckException
     */
    StoreDto queryStoreByName(String storeName) throws BusinessCheckException;

    /**
     * 根据店铺ID查询店铺信息
     *
     * @param id 店铺ID
     * @return
     * @throws BusinessCheckException
     */
    StoreDto queryStoreDtoById(Integer id) throws BusinessCheckException;

    /**
     * 更新店铺状态
     *
     * @param id       店铺ID
     * @param operator 操作人
     * @param status   状态
     * @throws BusinessCheckException
     */
    void updateStatus(Integer id, String operator, String status) throws BusinessCheckException;

    /**
     * 根据条件查询店铺
     * */
    List<MtStore> queryStoresByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 根据距离查找店铺
     * @param latitude
     * @param longitude
     * @return
     * */
    List<MtStore> queryByDistance(String keyword, String latitude, String longitude);
}
