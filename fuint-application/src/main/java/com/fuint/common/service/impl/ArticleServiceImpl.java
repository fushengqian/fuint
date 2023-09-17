package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.ArticleDto;
import com.fuint.common.service.ArticleService;
import com.fuint.common.service.MerchantService;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtArticleMapper;
import com.fuint.repository.model.MtArticle;
import com.fuint.common.service.SettingService;
import com.fuint.common.enums.StatusEnum;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

/**
 * 文章服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<MtArticleMapper, MtArticle> implements ArticleService {

    @Resource
    private MtArticleMapper mtArticleMapper;

    @Resource
    private SettingService settingService;

    @Resource
    private MerchantService merchantService;

    /**
     * 分页查询文章列表
     *
     * @param paginationRequest
     * @return
     */
    @Override
    public PaginationResponse<ArticleDto> queryArticleListByPagination(PaginationRequest paginationRequest) {
        Page<MtArticle> pageHelper = PageHelper.startPage(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        LambdaQueryWrapper<MtArticle> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtArticle::getStatus, StatusEnum.DISABLE.getKey());

        String title = paginationRequest.getSearchParams().get("title") == null ? "" : paginationRequest.getSearchParams().get("title").toString();
        if (StringUtils.isNotBlank(title)) {
            lambdaQueryWrapper.like(MtArticle::getTitle, title);
        }
        String status = paginationRequest.getSearchParams().get("status") == null ? "" : paginationRequest.getSearchParams().get("status").toString();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtArticle::getStatus, status);
        }
        String merchantId = paginationRequest.getSearchParams().get("merchantId") == null ? "" : paginationRequest.getSearchParams().get("merchantId").toString();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.eq(MtArticle::getMerchantId, merchantId);
        }
        String merchantNo = paginationRequest.getSearchParams().get("merchantNo") == null ? "" : paginationRequest.getSearchParams().get("merchantNo").toString();
        Integer mchId = merchantService.getMerchantId(merchantNo);
        if (mchId > 0) {
            lambdaQueryWrapper.eq(MtArticle::getMerchantId, mchId);
        }
        String storeId = paginationRequest.getSearchParams().get("storeId") == null ? "" : paginationRequest.getSearchParams().get("storeId").toString();
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtArticle::getStoreId, 0)
                    .or()
                    .eq(MtArticle::getStoreId, storeId));
        }
        lambdaQueryWrapper.orderByAsc(MtArticle::getSort);
        List<MtArticle> articleList = mtArticleMapper.selectList(lambdaQueryWrapper);
        List<ArticleDto> dataList = new ArrayList<>();

        String basePath = settingService.getUploadBasePath();
        for (MtArticle mtArticle : articleList) {
             ArticleDto articleDto = new ArticleDto();
             BeanUtils.copyProperties(mtArticle, articleDto);
             articleDto.setImage(basePath + mtArticle.getImage());
             dataList.add(articleDto);
        }

        PageRequest pageRequest = PageRequest.of(paginationRequest.getCurrentPage(), paginationRequest.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<ArticleDto> paginationResponse = new PaginationResponse(pageImpl, ArticleDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加文章
     *
     * @param articleDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增文章")
    public MtArticle addArticle(ArticleDto articleDto) {
        MtArticle mtArticle = new MtArticle();
        mtArticle.setTitle(articleDto.getTitle());
        mtArticle.setBrief(articleDto.getBrief());
        mtArticle.setMerchantId(articleDto.getMerchantId());
        mtArticle.setStoreId(articleDto.getStoreId() == null ? 0 : articleDto.getStoreId());
        mtArticle.setUrl(articleDto.getUrl());
        mtArticle.setClick(0l);
        mtArticle.setStatus(StatusEnum.ENABLED.getKey());
        mtArticle.setImage(articleDto.getImage());
        mtArticle.setDescription(articleDto.getDescription());
        mtArticle.setOperator(articleDto.getOperator());
        mtArticle.setUpdateTime(new Date());
        mtArticle.setCreateTime(new Date());
        mtArticle.setSort(articleDto.getSort());
        mtArticle.setMerchantId(articleDto.getMerchantId());
        Integer id = mtArticleMapper.insert(mtArticle);
        if (id > 0) {
            return mtArticle;
        } else {
            return null;
        }
    }

    /**
     * 根据ID获取文章
     *
     * @param id 文章ID
     * @return
     */
    @Override
    public MtArticle queryArticleById(Integer id) {
        return mtArticleMapper.selectById(id);
    }

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return
     */
    @Override
    public ArticleDto getArticleDetail(Integer id) {
        MtArticle mtArticle = mtArticleMapper.selectById(id);
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(mtArticle, articleDto);
        String baseImage = settingService.getUploadBasePath();
        articleDto.setImage(baseImage + mtArticle.getImage());
        return articleDto;
    }

    /**
     * 编辑文章
     *
     * @param  articleDto
     * @throws BusinessCheckException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "编辑文章")
    public MtArticle updateArticle(ArticleDto articleDto) throws BusinessCheckException {
        MtArticle mtArticle = queryArticleById(articleDto.getId());
        if (mtArticle == null) {
            throw new BusinessCheckException("该文章状态异常");
        }
        mtArticle.setId(articleDto.getId());
        if (articleDto.getImage() != null) {
            mtArticle.setImage(articleDto.getImage());
        }
        if (articleDto.getTitle() != null) {
            mtArticle.setTitle(articleDto.getTitle());
        }
        if (articleDto.getBrief() != null) {
            mtArticle.setBrief(articleDto.getBrief());
        }
        if (articleDto.getClick() != null) {
            mtArticle.setClick(articleDto.getClick());
        }
        if (articleDto.getMerchantId() != null) {
            mtArticle.setMerchantId(articleDto.getMerchantId());
        }
        if (articleDto.getMerchantId() != null) {
            mtArticle.setMerchantId(articleDto.getMerchantId());
        }
        if (articleDto.getStoreId() != null) {
            mtArticle.setStoreId(articleDto.getStoreId());
        }
        if (articleDto.getDescription() != null) {
            mtArticle.setDescription(articleDto.getDescription());
        }
        if (articleDto.getOperator() != null) {
            mtArticle.setOperator(articleDto.getOperator());
        }
        if (articleDto.getStatus() != null) {
            mtArticle.setStatus(articleDto.getStatus());
        }
        if (articleDto.getUrl() != null) {
            mtArticle.setUrl(articleDto.getUrl());
        }
        if (articleDto.getSort() != null) {
            mtArticle.setSort(articleDto.getSort());
        }
        mtArticle.setUpdateTime(new Date());
        mtArticleMapper.updateById(mtArticle);
        return mtArticle;
    }

    @Override
    public List<MtArticle> queryArticleListByParams(Map<String, Object> params) {
        String status =  params.get("status") == null ? StatusEnum.ENABLED.getKey(): params.get("status").toString();
        String storeId =  params.get("storeId") == null ? "" : params.get("storeId").toString();
        String title = params.get("title") == null ? "" : params.get("title").toString();
        String merchantId = params.get("merchantId") == null ? "" : params.get("merchantId").toString();

        LambdaQueryWrapper<MtArticle> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(merchantId)) {
            lambdaQueryWrapper.like(MtArticle::getMerchantId, merchantId);
        }
        if (StringUtils.isNotBlank(title)) {
            lambdaQueryWrapper.like(MtArticle::getTitle, title);
        }
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtArticle::getStatus, status);
        }
        if (StringUtils.isNotBlank(storeId)) {
            lambdaQueryWrapper.and(wq -> wq
                              .eq(MtArticle::getStoreId, 0)
                              .or()
                              .eq(MtArticle::getStoreId, storeId));
        }

        lambdaQueryWrapper.orderByAsc(MtArticle::getSort);
        List<MtArticle> dataList = mtArticleMapper.selectList(lambdaQueryWrapper);
        String baseImage = settingService.getUploadBasePath();

        if (dataList.size() > 0) {
            for (MtArticle article : dataList) {
                 article.setImage(baseImage + article.getImage());
            }
        }

        return dataList;
    }
}
