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
 * 焦点图业务接口
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
     * @return
     */
    MtBanner addBanner(BannerDto reqBannerDto) throws BusinessCheckException;

    /**
     * 根据ID获取Banner信息
     *
     * @param id Banner ID
     * @throws BusinessCheckException
     * @return
     */
    MtBanner queryBannerById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID删除焦点图
     *
     * @param id ID
     * @param operator 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteBanner(Integer id, String operator) throws BusinessCheckException;

    /**
     * 更新焦点图
     * @param bannerDto
     * @throws BusinessCheckException
     * @return
     * */
    MtBanner updateBanner(BannerDto bannerDto) throws BusinessCheckException;

    /**
     * 根据条件搜索焦点图
     *
     * @param params 查询参数
     * @throws BusinessCheckException
     * @return
     * */
    List<MtBanner> queryBannerListByParams(Map<String, Object> params) throws BusinessCheckException;
}
