package com.fuint.application.service.store;

import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;
import com.fuint.application.dao.entities.MtStore;
import com.fuint.application.dto.MtStoreDto;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 店铺业务接口
 * Created by FSQ
 * Contact wx fsq_better
 * Site https://www.fuint.cn
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
     * 保存店铺信息
     *
     * @param reqStoreDto
     * @throws BusinessCheckException
     */
    MtStore saveStore(MtStoreDto reqStoreDto) throws BusinessCheckException;

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
    MtStoreDto queryStoreByName(String storeName) throws BusinessCheckException, InvocationTargetException, IllegalAccessException;

    /**
     * 根据店铺ID查询店铺信息
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
     * 根据条件查询店铺
     * */
    List<MtStore> queryStoresByParams(Map<String, Object> params) throws BusinessCheckException;

    /**
     * 根据距离查找店铺
     * @param latitude
     * @param longitude
     * @return
     * */
    List<MtStore> queryByDistance(String latitude, String longitude);
}
