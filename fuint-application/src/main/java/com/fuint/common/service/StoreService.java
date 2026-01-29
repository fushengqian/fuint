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
    PaginationResponse<StoreDto> queryStoreListByPagination(PaginationRequest paginationRequest);

    /**
     * 保存店铺信息
     *
     * @param  reqStoreDto
     * @throws BusinessCheckException
     * @return
     */
    MtStore saveStore(StoreDto reqStoreDto) throws BusinessCheckException;

    /**
     * 获取系统默认店铺
     *
     * @param merchantNo 商户号
     * @return
     */
    MtStore getDefaultStore(String merchantNo);

    /**
     * 根据店铺ID获取店铺信息
     *
     * @param  id 店铺ID
     * @return
     */
    MtStore queryStoreById(Integer id);

    /**
     * 根据店铺名称获取店铺信息
     *
     * @param  storeName 店铺名称
     * @return
     */
    StoreDto queryStoreByName(String storeName);

    /**
     * 根据店铺ID查询店铺信息
     *
     * @param  id 店铺ID
     * @return
     */
    StoreDto queryStoreDtoById(Integer id);

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
    List<MtStore> queryStoresByParams(Map<String, Object> params);

    /**
     * 获取我的店铺列表
     *
     * @param merchantId 商户ID
     * @param storeId 店铺ID
     * @param status 状态
     * @return
     * */
    List<MtStore> getMyStoreList(Integer merchantId, Integer storeId, String status);

    /**
     * 根据距离远近查找店铺
     *
     * @param merchantNo 商户号
     * @param keyword 关键字
     * @param latitude 维度
     * @param longitude 经度
     * @return
     * */
    List<StoreInfo> queryByDistance(String merchantNo, String keyword, String latitude, String longitude);

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

    /**
     * 根据地址获取经纬度
     *
     * @param address 地址
     * @return
     * */
    Map<String, Object> getLatAndLngByAddress(String address);

    /**
     * 获取步行距离
     *
     * @param origin 起点经纬度 格式如：116.434446,39.90816
     * @param destination 终点经纬度 格式如：116.434307,39.90909
     * @return
     * */
    Double getDistance(String origin, String destination);

}
