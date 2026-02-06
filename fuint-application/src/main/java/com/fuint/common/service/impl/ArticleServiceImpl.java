package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.ArticleDto;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.ArticlePage;
import com.fuint.common.service.ArticleService;
import com.fuint.common.service.MerchantService;
import com.fuint.common.service.SettingService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.CommonUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtArticleMapper;
import com.fuint.repository.model.MtArticle;
import com.fuint.repository.model.MtStore;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文章服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class ArticleServiceImpl extends ServiceImpl<MtArticleMapper, MtArticle> implements ArticleService {

    private MtArticleMapper mtArticleMapper;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 分页查询文章列表
     *
     * @param articlePage
     * @return
     */
    @Override
    public PaginationResponse<ArticleDto> queryArticleListByPagination(ArticlePage articlePage) {
        Page<MtArticle> pageHelper = PageHelper.startPage(articlePage.getPage(), articlePage.getPageSize());
        LambdaQueryWrapper<MtArticle> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtArticle::getStatus, StatusEnum.DISABLE.getKey());

        String title = articlePage.getTitle();
        if (StringUtils.isNotBlank(title)) {
            lambdaQueryWrapper.like(MtArticle::getTitle, title);
        }
        String status = articlePage.getStatus();
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.eq(MtArticle::getStatus, status);
        }
        Integer merchantId = articlePage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtArticle::getMerchantId, merchantId);
        }
        String merchantNo = articlePage.getMerchantNo();
        Integer mchId = merchantService.getMerchantId(merchantNo);
        if (mchId > 0) {
            lambdaQueryWrapper.eq(MtArticle::getMerchantId, mchId);
        }
        Integer storeId = articlePage.getStoreId();
        if (storeId != null && storeId > 0) {
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

        PageRequest pageRequest = PageRequest.of(articlePage.getPage(), articlePage.getPageSize());
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
     * @param articleDto 文章参数
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "新增文章")
    public MtArticle addArticle(ArticleDto articleDto) throws BusinessCheckException {
        MtArticle mtArticle = new MtArticle();
        mtArticle.setTitle(articleDto.getTitle());
        mtArticle.setBrief(articleDto.getBrief());
        Integer storeId = articleDto.getStoreId() == null ? 0 : articleDto.getStoreId();
        if (articleDto.getMerchantId() == null || articleDto.getMerchantId() <= 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null) {
                articleDto.setMerchantId(mtStore.getMerchantId());
            }
        }
        mtArticle.setMerchantId(articleDto.getMerchantId());

        if (mtArticle.getMerchantId() == null || mtArticle.getMerchantId() < 1) {
            throw new BusinessCheckException("平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        mtArticle.setStoreId(storeId);
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
     * @param articleId 文章ID
     * @return
     */
    @Override
    public MtArticle queryArticleById(Integer articleId) {
        return mtArticleMapper.selectById(articleId);
    }

    /**
     * 根据ID获取文章详情
     *
     * @param articleId 文章ID
     * @return
     */
    @Override
    public ArticleDto getArticleDetail(Integer articleId) {
        MtArticle mtArticle = mtArticleMapper.selectById(articleId);
        if (mtArticle == null) {
            return null;
        }
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(mtArticle, articleDto);
        String baseImage = settingService.getUploadBasePath();
        articleDto.setImage(baseImage + mtArticle.getImage());
        articleDto.setDescription(CommonUtil.fixVideo(mtArticle.getDescription()));
        return articleDto;
    }

    /**
     * 编辑文章
     *
     * @param  articleDto 文章参数
     * @throws BusinessCheckException
     * @return
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

    /**
     * 根据条件搜索文章
     *
     * @param params 搜索条件
     * @return
     * */
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
