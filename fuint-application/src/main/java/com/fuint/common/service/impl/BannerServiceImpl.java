package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtBanner;
import com.fuint.common.dto.BannerDto;
import com.fuint.common.service.BannerService;
import com.fuint.common.service.SettingService;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.mapper.MtBannerMapper;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

/**
 * banner服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class BannerServiceImpl extends ServiceImpl<MtBannerMapper, MtBanner> implements BannerService {

    private static final Logger log = LoggerFactory.getLogger(BannerServiceImpl.class);

    @Resource
    private MtBannerMapper mtBannerMapper;

    @Resource
    private SettingService settingService;

    /**
     * 分页查询Banner列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<MtBanner> queryBannerListByPagination(PaginationRequest paginationRequest) {
        Page<MtBanner> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtBanner> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBanner::getStatus, StatusEnum.DISABLE.getKey());

        String title = paginationRequest.getSearchParams().get("title") == null ? "" : paginationRequest.getSearchParams().get("title").toString();
        if (StringUtils.isNotBlank(title)) {
            lambdaQueryWrapper.like(MtBanner::getTitle, title);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBanner::getStatus, status);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.eq(MtBanner::getStoreId, storeId);
        }

        lambdaQueryWrapper.orderByAsc(MtBanner::getSort);
        List<MtBanner> dataList = mtBannerMapper.selectList(lambdaQueryWrapper);

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<MtBanner> paginationResponse = new PaginationResponse(pageImpl, MtBanner.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加Banner信息
     *
     * @param bannerDto
     */
    @Override
    @OperationServiceLog(description = "新增Banner图")
    public MtBanner addBanner(BannerDto bannerDto) {
        MtBanner mtBanner = new MtBanner();
        mtBanner.setTitle(bannerDto.getTitle());
        mtBanner.setStoreId(bannerDto.getStoreId() == null ? 0 : bannerDto.getStoreId());
        mtBanner.setUrl(bannerDto.getUrl());
        mtBanner.setStatus(StatusEnum.ENABLED.getKey());
        mtBanner.setImage(bannerDto.getImage());
        mtBanner.setDescription(bannerDto.getDescription());
        mtBanner.setOperator(bannerDto.getOperator());
        mtBanner.setUpdateTime(new Date());
        mtBanner.setCreateTime(new Date());
        mtBanner.setSort(bannerDto.getSort());

        Integer id = mtBannerMapper.insert(mtBanner);
        if (id > 0) {
            return mtBanner;
        } else {
            return null;
        }
    }

    /**
     * 根据ID获取Banner信息
     *
     * @param id BannerID
     */
    @Override
    public MtBanner queryBannerById(Integer id) {
        return mtBannerMapper.selectById(id);
    }

    /**
     * 根据ID删除Banner信息
     *
     * @param id BannerID
     * @param operator 操作人
     */
    @Override
    @OperationServiceLog(description = "删除Banner图")
    public void deleteBanner(Integer id, String operator) {
        MtBanner mtBanner = this.queryBannerById(id);
        if (null == mtBanner) {
            return;
        }

        mtBanner.setStatus(StatusEnum.DISABLE.getKey());
        mtBanner.setUpdateTime(new Date());

        mtBannerMapper.updateById(mtBanner);
    }

    /**
     * 修改Banner
     *
     * @param bannerDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional
    @OperationServiceLog(description = "更新Banner图")
    public MtBanner updateBanner(BannerDto bannerDto) throws BusinessCheckException {
        MtBanner MtBanner = this.queryBannerById(bannerDto.getId());
        if (MtBanner == null) {
            throw new BusinessCheckException("该Banner状态异常");
        }

        MtBanner.setId(bannerDto.getId());
        if (bannerDto.getImage() != null) {
            MtBanner.setImage(bannerDto.getImage());
        }
        if (bannerDto.getTitle() != null) {
            MtBanner.setTitle(bannerDto.getTitle());
        }
        if (bannerDto.getStoreId() != null) {
            MtBanner.setStoreId(bannerDto.getStoreId());
        }
        if (bannerDto.getDescription() != null) {
            MtBanner.setDescription(bannerDto.getDescription());
        }
        if (bannerDto.getOperator() != null) {
            MtBanner.setOperator(bannerDto.getOperator());
        }
        if (bannerDto.getStatus() != null) {
            MtBanner.setStatus(bannerDto.getStatus());
        }
        if (bannerDto.getUrl() != null) {
            MtBanner.setUrl(bannerDto.getUrl());
        }
        if (bannerDto.getSort() != null) {
            MtBanner.setSort(bannerDto.getSort());
        }
        MtBanner.setUpdateTime(new Date());

        mtBannerMapper.updateById(MtBanner);

        return MtBanner;
    }

    @Override
    public List<MtBanner> queryBannerListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String title = params.get("title") == null ? "" : params.get("title").toString();

        LambdaQueryWrapper<MtBanner> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(title)) {
            lambdaQueryWrapper.like(MtBanner::getTitle, title);
        }
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtBanner::getStatus, status);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                              .eq(MtBanner::getStoreId, 0)
                              .or()
                              .eq(MtBanner::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByAsc(MtBanner::getSort);
        List<MtBanner> dataList = mtBannerMapper.selectList(lambdaQueryWrapper);
        String baseImage = settingService.getUploadBasePath();

        if (dataList.size() > 0) {
            for (MtBanner banner : dataList) {
                 banner.setImage(baseImage + banner.getImage());
            }
        }

        return dataList;
    }
}
