package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtBanner;
import com.fuint.common.dto.BannerDto;
import com.fuint.framework.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * banner业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface BannerService extends IService<MtBanner> {

    /**
     * 分页查询列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<MtBanner> queryBannerListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加Banner
     *
     * @param reqBannerDto
     * @throws BusinessCheckException
     */
    MtBanner addBanner(BannerDto reqBannerDto) throws BusinessCheckException;

    /**
     * 根据ID获取Banner信息
     *
     * @param id Banner ID
     * @throws BusinessCheckException
     */
    MtBanner queryBannerById(Integer id) throws BusinessCheckException;

    /**
     * 根据Banner ID 删除
     *
     * @param id       ID
     * @param operator 操作人
     * @throws BusinessCheckException
     */
    void deleteBanner(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新Banner
     * @param bannerDto
     * @throws BusinessCheckException
     * */
    MtBanner updateBanner(BannerDto bannerDto) throws BusinessCheckException;

    /**
     * 根据条件搜索Banner
     * */
    List<MtBanner> queryBannerListByParams(Map<String, Object> params) throws BusinessCheckException;

}
