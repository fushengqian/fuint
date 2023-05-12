package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.ArticleDto;
import com.fuint.framework.pagination.PaginationRequest;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.model.MtArticle;
import com.fuint.framework.exception.BusinessCheckException;
import java.util.List;
import java.util.Map;

/**
 * 文章业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface ArticleService extends IService<MtArticle> {

    /**
     * 分页查询文章列表
     *
     * @param paginationRequest
     * @return
     */
    PaginationResponse<ArticleDto> queryArticleListByPagination(PaginationRequest paginationRequest) throws BusinessCheckException;

    /**
     * 添加文章
     *
     * @param  articleDto
     * @throws BusinessCheckException
     */
    MtArticle addArticle(ArticleDto articleDto) throws BusinessCheckException;

    /**
     * 根据ID获取文章信息
     *
     * @param  id 文章ID
     * @throws BusinessCheckException
     */
    MtArticle queryArticleById(Integer id) throws BusinessCheckException;

    /**
     * 根据ID获取文章详情
     *
     * @param  id 文章ID
     * @throws BusinessCheckException
     */
    ArticleDto getArticleDetail(Integer id) throws BusinessCheckException;

    /**
     * 更新文章
     * @param  articleDto
     * @throws BusinessCheckException
     * */
    MtArticle updateArticle(ArticleDto articleDto) throws BusinessCheckException;

    /**
     * 根据条件搜索文章
     * */
    List<MtArticle> queryArticleListByParams(Map<String, Object> params) throws BusinessCheckException;

}
