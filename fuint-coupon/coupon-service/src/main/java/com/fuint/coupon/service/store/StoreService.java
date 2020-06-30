package com.fuint.coupon.service.store;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.coupon.dao.entities.MtStore;
import com.fuint.coupon.dto.MtStoreDto;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 店铺业务接口
 * Created by zach 20190820
 */
public interface StoreService {

    /**
     * 分页查询店铺列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtStore> queryStoreListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;


    /**
     * 添加店铺
     *
     * @param reqStoreDto
     * @throws BusinessCheckException
     */
    MtStore addStore(MtStoreDto reqStoreDto) throws BusinessCheckException;

    /**
     * 修改店铺
     *
     * @param reqStoreDto
     * @throws BusinessCheckException
     */
    MtStore updateStore(MtStoreDto reqStoreDto) throws BusinessCheckException;

    /**
     * 根据店铺ID获取店铺信息
     *
     * @param id 店铺ID
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
    MtStoreDto queryStoreByName(String storeName) throws BusinessCheckException, InvocationTargetException, IllegalAccessException;

    /**
     * 根据店铺ID获取店铺响应DTO
     *
     * @param id 店铺ID
     * @return
     * @throws BusinessCheckException
     */
    MtStoreDto queryStoreDtoById(Integer id) throws BusinessCheckException, InvocationTargetException, IllegalAccessException;

    /**
     * 根据店铺ID 删除店铺信息
     *
     * @param id       店铺ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteStore(Integer id, String operator) throws BusinessCheckException;

    /**
     * 根据参数查询店铺信息
     * @param params
     * @return
     * @throws BusinessCheckException
     */
    public List<MtStore> queryEffectiveStoreRange(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 更改状态(禁用)
     *
     * @param ids
     * @throws com.fuint.exception.BusinessCheckException
     */
    Integer updateStatus(List<Integer> ids, String statusEnum) throws BusinessCheckException;

    /**
     * 根据条件搜索店铺
     * */
    public List<MtStore> queryStoresByParams(Map<String, Object> params) throws BusinessCheckException;

}
