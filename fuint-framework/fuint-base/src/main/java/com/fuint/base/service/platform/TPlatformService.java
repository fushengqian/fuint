package com.fuint.base.service.platform;

import com.fuint.base.dao.entities.TPlatform;
import com.fuint.base.dao.pagination.PaginationRequest;
import com.fuint.base.dao.pagination.PaginationResponse;
import com.fuint.exception.BusinessCheckException;

import java.util.List;

/**
 * 平台接口服务类
 * <p/>
 * Created by FSQ
 * Contact wx fsq_better
 */
public interface TPlatformService {

    /**
     * 根据ID获取平台信息
     *
     * @param id 平台ID
     * @return 平台信息实体
     */
    TPlatform getPlatformById(Long id);

    /**
     * 获取状态为有效的平台信息列表
     *
     * @return 状态为有效的平台信息列表
     */
    List<TPlatform> getPlatforms();

    /**
     * 分页数据获取
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<TPlatform> findPlatformByPagination(PaginationRequest paginationRequest);

    /**
     * 新增平台
     *
     * @param tPlatform
     */
    void addPlatform(TPlatform tPlatform) throws BusinessCheckException;

    /**
     * 删除平台
     *
     * @param platformId
     */
    void deletePlatform(Long platformId) throws BusinessCheckException;
}
