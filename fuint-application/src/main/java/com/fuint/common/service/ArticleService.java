package com.fuint.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuint.common.dto.ArticleDto;
import com.fuint.common.param.ArticlePage;
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
     * @param articlePage
     * @return
     */
    PaginationResponse<ArticleDto> queryArticleListByPagination(ArticlePage articlePage);

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
     * @return
     */
    MtArticle queryArticleById(Integer id);

    /**
     * 根据ID获取文章详情
     *
     * @param  id 文章ID
     */
    ArticleDto getArticleDetail(Integer id);

    /**
     * 更新文章
     * @param  articleDto
     * @throws BusinessCheckException
     * @return
     * */
    MtArticle updateArticle(ArticleDto articleDto) throws BusinessCheckException;

    /**
     * 根据条件搜索文章
     *
     * @param params
     * @return
     * */
    List<MtArticle> queryArticleListByParams(Map<String, Object> params) throws BusinessCheckException;

}
